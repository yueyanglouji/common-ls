package l.s.common.mail;

import org.springframework.core.io.Resource;

public class MailResourcePair {

    private String name;

    private Resource resource;

    public MailResourcePair(String name, Resource resource){
        this.name = name;
        this.resource = resource;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }
}
