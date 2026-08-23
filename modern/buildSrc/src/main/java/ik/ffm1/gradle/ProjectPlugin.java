package ik.ffm1.gradle;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.DuplicatesStrategy;
import org.gradle.api.logging.Logger;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.specs.Specs;
import org.gradle.api.tasks.Copy;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceTask;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.jvm.tasks.Jar;

import com.google.common.collect.ImmutableMap;

import ik.ffm1.gradle.extensions.ModExtension;
import ik.ffm1.gradle.tasks.BuildMod;
import ik.ffm1.gradle.tasks.MergeMixin;
import ik.ffm1.gradle.tasks.Validate;

/**
 * FFM1 (Modern) project plugin.
 *
 * <p>Handles the "modern" multi-version build (Minecraft 1.20.4+):
 * <ul>
 *     <li>{@code :main} - the mod core jar ({@code mod.jar}) containing {@code MainMod} + Fabric platform entry + assets</li>
 *     <li>{@code :modloader} - Fabric-side loader (version mapping + patched-classes mixin plugin)</li>
 *     <li>{@code :fabric-mixin} - merged per-version mixins jar</li>
 *     <li>{@code :game:fabric*} - per-version Fabric core jars (API implementation + mixins, loom)</li>
 *     <li>{@code :game:neoforge*} - per-version standalone NeoForge mod jars (ModDevGradle)</li>
 *     <li>{@code buildMod} - assembles the Fabric single-jar-multi-version distribution</li>
 * </ul>
 */
public class ProjectPlugin implements Plugin<Project> {

    @Override
    public void apply(Project root) {
        Logger logger = root.getLogger();

        logger.lifecycle("Welcome to Forge & Fabric Mixin One (Modern)!");

        File prop = root.file("mod.properties");

        if (!prop.exists()) {
            try {
                FileOutputStream fos = new FileOutputStream(prop);
                InputStream input = ProjectPlugin.class.getResourceAsStream("/mod.properties");
                byte[] bytes = new byte[4096];
                int read;

                while ((read = input.read(bytes)) > 0) {
                    fos.write(bytes, 0, read);
                }

                input.close();
                fos.close();
            } catch (IOException e) {}
        }

        Properties properties = new Properties();

        try {
            FileInputStream fis = new FileInputStream(prop);
            properties.load(fis);
            fis.close();
        } catch (IOException e) {}

        Map<String, String> expand = new HashMap<>();

        for (Entry<Object, Object> entry : properties.entrySet()) {
            expand.put(entry.getKey().toString().toUpperCase(), entry.getValue().toString());
        }

        root.allprojects(project -> {
            project.getExtensions().add("mod", new ModExtension(properties));
        });

        String modName = properties.getProperty("mod_name", "Mod").replace(" ", "");

        root.allprojects(project -> {
            project.getTasks().withType(Jar.class, task -> {
                if (project.getName().equals("main")) {
                    task.getArchiveBaseName().set(modName);
                    task.getArchiveVersion().set(properties.getProperty("mod_version"));
                } else if (project.getName().startsWith("neoforge")) {
                    task.getArchiveBaseName().set(modName);
                    task.getArchiveVersion().set(properties.getProperty("mod_version"));
                    task.getArchiveClassifier().set("neoforge-" + project.getName().substring("neoforge".length()));
                }
            });
        });

        root.allprojects(project -> {
            Object ext = project.getExtensions().findByName("java");

            if (ext instanceof JavaPluginExtension) {
                JavaPluginExtension java = (JavaPluginExtension) ext;

                java.getSourceSets().all(sourceSet -> {
                    TaskProvider<Copy> provider = project.getTasks().register(sourceSet.getTaskName("process", "Sources"), Copy.class, task -> {
                        File output = new File(project.getBuildDir(), "sources/" + sourceSet.getName());

                        task.from(sourceSet.getJava());
                        task.into(output);
                        task.expand(expand, detail -> {
                            detail.getEscapeBackslash().set(true);
                        });

                        task.getOutputs().upToDateWhen(Specs.SATISFIES_NONE);
                    });

                    project.getTasks().named(sourceSet.getCompileJavaTaskName(), task -> {
                        task.dependsOn(provider);
                        ((SourceTask) task).setSource(new File(project.getBuildDir(), "sources/" + sourceSet.getName()));
                    });

                    project.getTasks().named(sourceSet.getProcessResourcesTaskName(), task -> {
                        Copy copy = (Copy) task;
                        copy.from(sourceSet.getResources(), spec -> {
                            spec.include("**/*.properties", "**/*.json", "**/*.toml", "**/*.mcmeta", "META-INF/services/*");
                            spec.expand(expand, detail -> {
                                detail.getEscapeBackslash().set(true);
                            });
                            spec.setDuplicatesStrategy(DuplicatesStrategy.INCLUDE);
                        });

                        task.getOutputs().upToDateWhen(Specs.SATISFIES_NONE);
                    });
                });
            }
        });

        BuildMod build = root.getTasks().create("buildMod", BuildMod.class, task -> {
            task.setGroup("_Mod_");

            task.dependsOn(root.project(":modloader").getTasks().named("assemble"));
            task.dependsOn(root.project(":main").getTasks().named("assemble"));
            task.dependsOn(root.project(":fabric-mixin").getTasks().named("assemble"));

            task.launcher(root.project(":modloader"));
            task.mod(root.project(":main"));
        });

        Project mixin = root.project(":fabric-mixin");

        MergeMixin merge = mixin.getTasks().create("mergeMixin", MergeMixin.class);

        build.mustRunAfter(merge);
        build.core(mixin);

        mixin.getTasks().named("jar", task -> {
            merge.main(((Jar) task).getArchiveFile().get().getAsFile());
        });

        mixin.getTasks().named("assemble", task -> {
            task.finalizedBy(merge);
        });

        Project game = root.project(":game");

        game.subprojects(project -> {
            String t = null;

            if (project.getName().startsWith("neoforge")) {
                t = "neoforge";
            } else if (project.getName().startsWith("fabric")) {
                t = "fabric";
            }

            if (t == null) {
                return;
            }

            if (t.equals("fabric")) {
                build.dependsOn(project.getTasks().named("assemble"));
                // 仅编译期需要 modloader 的工具类（FabricQuiltUtils/VersionMapping），
                // 运行时由最终 jar 的 modloader 提供，避免在 loom 仓库集下解析其传递依赖。
                project.getDependencies().add("compileOnly", root.project(":modloader"));

                Validate validate = project.getTasks().create("validateApi", Validate.class, task -> {
                    task.dependsOn(game.project("api").getTasks().named("jar"));
                });

                game.project("api").getTasks().named("jar", jar -> {
                    validate.api(((Jar) jar).getArchiveFile().get().getAsFile());
                });

                project.getTasks().named("jar", jar -> {
                    File output = ((Jar) jar).getArchiveFile().get().getAsFile();

                    validate.impl(output);
                    merge.mixin(project.getName().substring("fabric".length()), output);
                });

                project.getTasks().named("assemble", assemble -> {
                    assemble.finalizedBy(validate);
                });

                mixin.getTasks().named("assemble", task -> {
                    task.dependsOn(project.getTasks().named("assemble"));
                });

                build.core(project);
                project.apply(ImmutableMap.of("from", game.file("fabric.gradle")));
            } else if (t.equals("neoforge")) {
                Validate validate = project.getTasks().create("validateApi", Validate.class, task -> {
                    task.dependsOn(game.project("api").getTasks().named("jar"));
                });

                game.project("api").getTasks().named("jar", jar -> {
                    validate.api(((Jar) jar).getArchiveFile().get().getAsFile());
                });

                project.getTasks().named("jar", jar -> {
                    validate.impl(((Jar) jar).getArchiveFile().get().getAsFile());
                });

                project.getTasks().named("assemble", assemble -> {
                    assemble.finalizedBy(validate);
                });

                project.apply(ImmutableMap.of("from", game.file("neoforge.gradle")));
            }
        });
    }
}
