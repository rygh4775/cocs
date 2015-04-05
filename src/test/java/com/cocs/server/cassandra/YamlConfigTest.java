package com.cocs.server.cassandra;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

import org.apache.cassandra.config.Config;
import org.apache.cassandra.utils.SkipNullRepresenter;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.yaml.snakeyaml.Dumper;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Loader;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Tag;

public class YamlConfigTest {

	@Test
	public void test() {
		// 1. reading an existing config-file,
	       InputStream input;
		try {
			input = new FileInputStream(new ClassPathResource("/cassandra_old.yaml").getFile());

	       Constructor constructor = new Constructor(Config.class);
//	       TypeDescription seedDesc = new TypeDescription(SeedProviderDef.class);
//	       seedDesc.putMapPropertyType("parameters", String.class, String.class);
//	       constructor.addTypeDescription(seedDesc);
	       Yaml cassandraConfYaml = new Yaml(new Loader(constructor));

	       Config conf = (Config) cassandraConfYaml.load(input);

	       System.out.println(conf.cluster_name);
	       conf.cluster_name = "changeClusterName2";
	       System.out.println(conf.commitlog_directory);
	       System.out.println(conf.saved_caches_directory);
	       System.out.println(conf.data_file_directories[0]);
	       // 2. change the setting in the Config-object
//	       SeedProviderDef spd = conf.seed_provider;
//	       spd.parameters.put("seeds", "192.168.1.2");

	       DumperOptions options = new DumperOptions();
//	       options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
//	       options.setDefaultScalarStyle(ScalarStyle.PLAIN);
//
	       SkipNullRepresenter representer = new SkipNullRepresenter();
	       representer.addClassTag(Config.class, Tag.YAML);

	       Dumper dumper = new Dumper(representer, options);

	       Yaml yamlWriter = new Yaml(new Loader(constructor), dumper);

	       // 3. and writing it back to another file.
	       Writer output = new FileWriter(new ClassPathResource("src/test/resources/cassandra_new.yaml").getPath());
	       yamlWriter.dump(conf, output);
	       
//	       Reader reader = new FileReader(fileName);
//	       Yaml myCassandraConfYaml = new Yaml(new Loader(constructor));
	       // This procedure looses these two dashes and makes the 
	       //configuration not
	       // usable for further processing in other tasks.
//	       Config myConf = (Config) myCassandraConfYaml.load(reader);
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
    }

}
