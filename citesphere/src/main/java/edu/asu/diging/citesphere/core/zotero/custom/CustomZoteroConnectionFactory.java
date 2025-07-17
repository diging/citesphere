package edu.asu.diging.citesphere.core.zotero.custom;

import org.springframework.social.zotero.connect.ZoteroConnectionFactory;
import org.springframework.social.oauth1.OAuth1Operations;

public class CustomZoteroConnectionFactory extends ZoteroConnectionFactory {

    public CustomZoteroConnectionFactory(String consumerKey, String consumerSecret) {
        super(consumerKey, consumerSecret);
    }

    @Override
    public OAuth1Operations getOAuthOperations() {
        OAuth1Operations ops = super.getOAuthOperations();
        return new CustomOAuth1Operations(ops);
    }
} 