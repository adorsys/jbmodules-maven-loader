package org.jboss.modules;

import org.apache.maven.repository.internal.MavenRepositorySystemSession;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.repository.LocalRepository;
import org.sonatype.aether.repository.RemoteRepository;

/**
 * A helper to boot the repository system and a repository system session.
 */
public class Booter
{

	public static RepositorySystem newRepositorySystem()
	{
		return ManualRepositorySystemFactory.newRepositorySystem();
	}

	public static RepositorySystemSession newRepositorySystemSession( RepositorySystem system )
	{
		String userhome = System.getProperty("user.home");
		MavenRepositorySystemSession session = new MavenRepositorySystemSession();
		LocalRepository localRepo = new LocalRepository( userhome + "/.m2/repository" );
		session.setLocalRepositoryManager( system.newLocalRepositoryManager( localRepo ) );
		return session;
	}

	public static RemoteRepository newCentralRepository()
	{
		return new RemoteRepository( "central", "default", "http://repo1.maven.org/maven2/" );
	}

}