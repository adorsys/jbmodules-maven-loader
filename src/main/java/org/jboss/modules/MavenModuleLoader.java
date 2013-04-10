package org.jboss.modules;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.jar.JarFile;

import org.jboss.modules.ModuleXmlParser.ResourceRootFactory;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.resolution.ArtifactRequest;
import org.sonatype.aether.resolution.ArtifactResolutionException;
import org.sonatype.aether.resolution.ArtifactResult;
import org.sonatype.aether.util.artifact.DefaultArtifact;

public class MavenModuleLoader extends ModuleLoader {

	private final LocalModuleLoader localLoader = new LocalModuleLoader();

	@Override
	public String toString() {
		return "MavenModuleLoader";
	}

	@Override
	protected Module preloadModule(ModuleIdentifier identifier) throws ModuleLoadException {
		Module module;
		try {
			module = localLoader.loadModule(identifier);
		} catch (ModuleLoadException e) {
			module = null;
		}
		if (module == null) {
			module = super.preloadModule(identifier);
		}
		return module;
	}

	@Override
	protected ModuleSpec findModule(ModuleIdentifier moduleIdentifier) throws ModuleLoadException {


		//		ModuleSpec.build(moduleIdentifier).addResourceRoot(ResourceLoaderSpec.createResourceLoaderSpec(ResourceLoaders.createJarResourceLoader("foo.jar", jarFile)))

		try {
			final RepositorySystem system = ManualRepositorySystemFactory.newRepositorySystem();;

			final RepositorySystemSession session = Booter.newRepositorySystemSession( system );

			Artifact artifact = new DefaultArtifact( "org.jboss.modules.mavenmodules:" + moduleIdentifier.getName() + ":module:"+ moduleIdentifier.getSlot());

			final RemoteRepository repo = Booter.newCentralRepository();

			ArtifactRequest artifactRequest = new ArtifactRequest();
			artifactRequest.setArtifact( artifact );
			//			artifactRequest.addRepository( repo );

			ArtifactResult artifactResult = system.resolveArtifact( session, artifactRequest );
			artifact = artifactResult.getArtifact();
			System.out.println( artifact + " resolved to  " + artifact.getFile() );
			ModuleSpec parseModuleXml = ModuleXmlParser.parseModuleXml(new ResourceRootFactory() {

				@Override
				public ResourceLoader createResourceLoader(String rootPath, String loaderPath, String loaderName) throws IOException {
					try {
						ArtifactRequest artifactRequest = new ArtifactRequest();
						artifactRequest.setArtifact( new DefaultArtifact( loaderPath ) );
						artifactRequest.addRepository( repo );

						ArtifactResult artifactResult = system.resolveArtifact( session, artifactRequest );

						final JarFile jarFile = new JarFile(artifactResult.getArtifact().getFile(), true);
						return new JarFileResourceLoader(loaderName, jarFile);
					} catch (ArtifactResolutionException e) {
						throw new IOException(e);
					}
				}
			}, "/", new FileInputStream(artifact.getFile()), artifact.getFile().getPath(), moduleIdentifier);
			return parseModuleXml;
		} catch (ArtifactResolutionException e) {
			e.printStackTrace();
			return null;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

}
