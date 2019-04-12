package edu.asu.diging.citesphere.core.model.bib;

import java.util.Map;
import com.google.common.collect.Maps;

public enum ItemType {
    ARTWORK("artwork"),
    ATTACHMENT("attachment"),
    AUDIO_RECORDING("audioRecording"),
    BILL("bill"),
    BLOG_POST("blogPost"),
    BOOK("book"),
    BOOK_SECTION("bookSection"),
    CASE("case"),
    COMPUTER_PROGRAM("computerProgram"),
    CONFERENCE_PAPER("conferencePaper"),
    DICTIONARY_ENTRY("dictionaryEntry"),
    DOCUMENT("document"),
    EMAIL("email"),
    ENCYCLOPEDIA_ARTICLE("encyclopediaArticle"),
    FILM("film"),
    FORUM_POST("forumPost"),
    HEARING("hearing"),
    INSTANT_MESSAGE("instantMessage"),
    INTERVIEW("interview"),
    JOURNAL_ARTICLE("journalArticle"),
    LETTER("letter"),
    MAGAZINE_ARTICLE("magazineArticle"),
    MANUSCRIPT("manuscript"),
    MAP("map"),
    NEWSPAPER_ARTICLE("newspaperArticle"),
    NOTE("note"),
    PATENT("patent"),
    PODCAST("podcast"),
    PRESENTATION("presentation"),
    RADIO_BROADCAST("radioBroadcast"),
    REPORT("report"),
    STATUTE("statute"),
    THESIS("thesis"),
    TV_BROADCAST("tvBroadcast"),
    VIDEO_RECORDIG("videoRecording"),
    WEBPAGE("webpage");
    
    final private String zoteroKey;
    
    final static Map<String, ItemType> index = Maps.newHashMapWithExpectedSize(ItemType.values().length);
    static {
        for (ItemType type : ItemType.values()) {
            index.put(type.zoteroKey, type);
        }
    }
    
    private ItemType(String zoteroKey) {
        this.zoteroKey = zoteroKey;
    }
    
    public static ItemType getByZoteroKey(String key) {
        return index.get(key);
    }
    
    public String getZoteroKey() {
        return zoteroKey;
    }
}
