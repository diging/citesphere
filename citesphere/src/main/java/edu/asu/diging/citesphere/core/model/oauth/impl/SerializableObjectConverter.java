package edu.asu.diging.citesphere.core.model.oauth.impl;

import org.apache.commons.codec.binary.Base64;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.util.SerializationUtils;

/**
 * Modeled after:
 * https://blog.couchbase.com/custom-token-store-spring-securtiy-oauth2/
 * 
 * @author jdamerow
 *
 */
public class SerializableObjectConverter {

    private SerializableObjectConverter() {
        throw new IllegalStateException();
    }

    public static String serialize(OAuth2Authentication object) {
        byte[] bytes = SerializationUtils.serialize(object);
        return Base64.encodeBase64String(bytes);
    }

    public static OAuth2Authentication deserialize(String encodedObject) {

        byte[] bytes = Base64.decodeBase64(encodedObject);
        return (OAuth2Authentication) SerializationUtils.deserialize(bytes);
    }

    public static String serializeToken(OAuth2AccessToken object) {
        byte[] bytes = SerializationUtils.serialize(object);
        return Base64.encodeBase64String(bytes);
    }

    public static OAuth2AccessToken deserializeToken(String encodedObject) {
        byte[] bytes = Base64.decodeBase64(encodedObject);
        return (OAuth2AccessToken) SerializationUtils.deserialize(bytes);
    }

    public static String serializeRefreshToken(OAuth2RefreshToken object) {
        byte[] bytes = SerializationUtils.serialize(object);
        return Base64.encodeBase64String(bytes);
    }

    public static OAuth2RefreshToken deserializeRefreshToken(String encodedObject) {
        byte[] bytes = Base64.decodeBase64(encodedObject);
        return (OAuth2RefreshToken) SerializationUtils.deserialize(bytes);
    }

}