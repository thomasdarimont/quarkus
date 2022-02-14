package io.quarkus.bootstrap.runner;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.security.cert.Certificate;
import java.util.Objects;

/**
 * An exploded jar resource
 */
public class ExplodedJarResource implements ClassLoadingResource {

    private final Path path;
    private final ManifestInfo manifestInfo;
    private ProtectionDomain protectionDomain;

    public static boolean isExplodedJarResource(Path resourcePath) {
        return resourcePath.toFile().isDirectory() && resourcePath.toString().endsWith(".jar");
    }

    public ExplodedJarResource(ManifestInfo manifestInfo, Path path) {
        this.path = path;
        this.manifestInfo = manifestInfo;
    }

    @Override
    public void init(ClassLoader runnerClassLoader) {
        try {
            URL url = path.toFile().toURI().toURL();
            this.protectionDomain = new ProtectionDomain(new CodeSource(url, (Certificate[]) null), null, runnerClassLoader,
                    null);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Unable to create protection domain for " + path, e);
        }
    }

    @Override
    public byte[] getResourceData(String resource) {

        Path resourcePath = path.resolve(resource);
        File resourceFile = resourcePath.toFile();
        if (!resourceFile.exists()) {
            return null;
        }

        try (InputStream is = Files.newInputStream(resourcePath)) {
            return is.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file entry " + resource, e);
        }
    }

    @Override
    public URL getResourceURL(String resource) {
        Path resourcePath = path.resolve(resource);
        File resourceFile = resourcePath.toFile();
        if (!resourceFile.exists()) {
            return null;
        }

        try {
            return resourceFile.toURI().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ManifestInfo getManifestInfo() {
        return manifestInfo;
    }

    @Override
    public ProtectionDomain getProtectionDomain() {
        return protectionDomain;
    }

    @Override
    public void close() {
        // NOOP
    }

    @Override
    public String toString() {
        return "ClassesFolderResource{" +
                path.getFileName() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ExplodedJarResource that = (ExplodedJarResource) o;
        return Objects.equals(manifestInfo, that.manifestInfo) && path.equals(that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(manifestInfo, path);
    }
}
