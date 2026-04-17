package com.example.datapi.config;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Host;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "aerospike")
public class AerospikeConfig {

    private List<AerospikeHost> hosts;
    private String namespace;
    private String set;

    @Bean
    public AerospikeClient aerospikeClient() {
        List<Host> aerospikeHosts = new ArrayList<>();
        for (AerospikeHost host : hosts) {
            aerospikeHosts.add(new Host(host.getHost(), host.getPort()));
        }
        return new AerospikeClient(new com.aerospike.client.policy.ClientPolicy(), 
            aerospikeHosts.toArray(new Host[0]));
    }

    @Bean
    public String aerospikeNamespace() {
        return namespace;
    }

    @Bean
    public String aerospikeSet() {
        return set;
    }

    // Getters and Setters
    public List<AerospikeHost> getHosts() {
        return hosts;
    }

    public void setHosts(List<AerospikeHost> hosts) {
        this.hosts = hosts;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getSet() {
        return set;
    }

    public void setSet(String set) {
        this.set = set;
    }

    public static class AerospikeHost {
        private String host;
        private int port;

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }
    }
}
