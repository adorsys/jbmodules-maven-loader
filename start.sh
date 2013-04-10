java -cp target/mml-jar-with-dependencies.jar -Dorg.jboss.boot.log.file=target/boot.log \
	-Dmodule.path=/Users/sso/Documents/dev/kurz/jboss-as-7.1.1.Final/modules \
	-Dlogging.configuration=logging.properties \
	-Dboot.module.loader=org.jboss.modules.MavenModuleLoader \
	org.jboss.modules.Main \
    -jaxpmodule "javax.xml.jaxp-provider" \
    de.adorsys.auth.spi:main
