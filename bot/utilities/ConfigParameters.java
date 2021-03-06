package com.raxa.bot.utilities;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import java.util.logging.Logger;

/**
 * This class acts as a parser for the configuration file.
 */
public final class ConfigParameters {

    private final static Logger LOGGER = Logger.getLogger(ConfigParameters.class.getSimpleName());

    /**
     * An inner class that holds all the positions of all the elements.
     */
    public final static class Elements {

        public Document doc;

        public Element configNode;

        public Element editors;

        public NodeList editorNodes;

        public Element commands;

        public Element counters;

        public NodeList commandNodes;

        public NodeList counterNodes;

        public Element filters;

        public NodeList filterNodes;

        public Element phrases;

        public NodeList phraseNodes;

        public Element regex;

        public NodeList regexNodes;
    }

    /**
     * An inner class that holds simple configurations, these fields should not
     * change once the bot is up, and nobody should be changing them live
     * anyway.
     */
    public final static class Configuration {

        public String account;

        public String clientID;

        public String password;

        public String joinedChannel;

        public String host;

        public int port;

        public String pubSub;

        public String streamerStatus;

        public String followage;

        public String youtubeApi;

        public String youtubeTitle;

        public String sqlURL;
        
        public String sqlMURL;

        public String sqlUser;

        public String sqlPass;
        
        public String ftpURL;
        
        public String ftpUser;
        
        public String ftpPass;

        public String channelID;

        public String botID;

        public String pubSubAuthToken;

        public String botWhisperToken;

        public String streamlabsToken;

        public int onlineCheckTimer;

        public int recentMessageCacheSize;

        public int numCounters;

        public String lottoStatus;
        
        public String rpgStatus;
        
        public String lottoAuth;

        public String lottoName;

        public String pyramidResponse;

        public String pings;
        
        public String discordToken;
        
        public String spotifyUrl;
        
        public String spotifyId;
        
        public String spotifySecret;
        
        public String spotifyStatus;

        @Override
        public String toString() {
            return "Configuration{"
                    + "account='" + account + '\''
                    + ", clientID='" + clientID + '\''
                    + ", password='" + password + '\''
                    + ", joinedChannel='" + joinedChannel + '\''
                    + ", host='" + host + '\''
                    + ", port=" + port
                    + ", pubSub='" + pubSub + '\''
                    + ", streamerStatus='" + streamerStatus + '\''
                    + ", followage='" + followage + '\''
                    + ", youtubeApi='" + youtubeApi + '\''
                    + ", youtubeTitle='" + youtubeTitle + '\''
                    + ", onlineCheckTimer=" + onlineCheckTimer
                    + ", recentMessageCacheSize=" + recentMessageCacheSize
                    + ", numCounters=" + numCounters
                    + ", sqlURL=" + sqlURL
                    + ", sqlMURL=" + sqlMURL
                    + ", sqlUser=" + sqlUser
                    + ", sqlPass=" + sqlPass
                    + ", ftpURL=" + ftpURL
                    + ", ftpUser=" + ftpUser
                    + ", ftpPass=" + ftpPass
                    + ", channelID=" + channelID
                    + ", botID=" + botID
                    + ", pubSubAuthToken=" + pubSubAuthToken
                    + ", botWhisperToken=" + botWhisperToken
                    + ", streamlabsToken=" + streamlabsToken
                    + ", discordToken=" + discordToken
                    + ", spotifyUrl=" + spotifyUrl
                    + ", spotifyId=" + spotifyId
                    + ", spotifySecret=" + spotifySecret
                    + ", spotifyStatus=" + spotifyStatus
                    + ", lottoStatus=" + lottoStatus
                    +", rpgStatus=" + rpgStatus
                    + ", pyramidResponse='" + pyramidResponse + '\''
                    + '}';
        }
    }

    /*
     * Represent editor levels
     * per user basis
     */
    public final static class Editor {

        //twitch username check 
        //level internal usage for authorization beyond moderator status
        public String username;
        public int level;

        @Override
        public String toString() {
            return "Editor{"
                    + "username='" + username + '\''
                    + ", level=" + level
                    + '}';
        }
    }

    /**
     * Represents a single Command
     */
    public final static class Command {

        // A long string that signifies who has the credentials to use this command
        public String auth;

        // A command name
        public String name;

        // No clue what this is used for
        public String cdUntil;

        // Time in seconds for cooldown
        public String cooldown;

        // Flag for whenever this command is enabled/disabled
        public boolean disabled;

        // No clue what this is used for
        public String initialDelay;

        // No clue what this is used for
        public String interval;

        // No clue what this is used for
        public String repeating;

        // The sound to play when command is used?
        public String sound;

        // The text to display when this command is hit
        public String text;

        @Override
        public String toString() {
            return "Command{"
                    + "auth='" + auth + '\''
                    + ", name='" + name + '\''
                    + ", cdUntil='" + cdUntil + '\''
                    + ", cooldown='" + cooldown + '\''
                    + ", disabled=" + disabled
                    + ", initialDelay='" + initialDelay + '\''
                    + ", interval='" + interval + '\''
                    + ", repeating='" + repeating + '\''
                    + ", sound='" + sound + '\''
                    + ", text='" + text + '\''
                    + '}';
        }
    }

    /**
     * Represents a single counter, for counting a list of items
     */
    public final static class Counter {

        // The name of the counter
        public String name;

        // The current count
        public int count;

        @Override
        public String toString() {
            return "Counter{"
                    + "name='" + name + '\''
                    + ", count=" + count
                    + '}';
        }
    }

    /**
     * Represents a single filter, for counting a single filter
     */
    public final static class Filter {

        public boolean enabled;

        public String name;

        public String reason;

        public String seconds;

        @Override
        public String toString() {
            return "Filter{"
                    + "enabled='" + enabled + '\''
                    + ", name='" + name + '\''
                    + ", reason='" + reason + '\''
                    + ", seconds=" + seconds + '\''
                    + '}';
        }
    }

    public final static class FilterPhrase {

        public boolean enabled;

        public String phrase;

        public String reason;

        public String seconds;

        @Override
        public String toString() {
            return "FilterPhrase{"
                    + "enabled='" + enabled + '\''
                    + ", phrase='" + phrase + '\''
                    + ", reason='" + reason + '\''
                    + ", seconds=" + seconds + '\''
                    + '}';
        }
    }

    public final static class FilterRegex {

        public String name;
        
        public boolean enabled;

        public String content;

        public String reason;

        public String seconds;

        @Override
        public String toString() {
            return "FilterRegex{"
                    + "name='" + name + '\''
                    + "enabled='" + enabled + '\''
                    + ", content='" + content + '\''
                    + ", reason='" + reason + '\''
                    + ", seconds=" + seconds + '\''
                    + '}';
        }
    }

    /**
     * This method will parse the configuration and save off references to each
     * of the parsed XML file parts.
     *
     * @param configFile The configuration file path (.XML file path) from the
     * source code path
     *
     * @return A set of references of the XML file.
     *
     * @throws ParserConfigurationException If the file cannot be parsed (not a
     * valid XML file), this exception is thrown.
     *
     * @throws SAXException If the SAX parser fails to parse an element, this
     * exception is thrown.
     *
     * @throws IOException If the file cannot be read (no file exist, or wrong
     * path), this exception is thrown.
     */
    public Elements parseConfiguration(final String configFile)
            throws ParserConfigurationException, SAXException, IOException {

        // Declare the file, run a document (XML) parser through it
        // Read the entire file into RAM, and return a set of all the element
        // locations
        final File configurationFile = new File(configFile);
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder builder = factory.newDocumentBuilder();

        final Elements elements = new Elements();
        elements.doc = builder.parse(configurationFile);
        elements.doc.getDocumentElement().normalize();

        LOGGER.info("Completed reading the XML file");

        elements.configNode = (Element) elements.doc.getElementsByTagName("config").item(0);
        elements.editors = (Element) elements.doc.getElementsByTagName("editors").item(0);
        elements.editorNodes = elements.editors.getElementsByTagName("editor");
        elements.commands = (Element) elements.doc.getElementsByTagName("commands").item(0);
        elements.commandNodes = elements.commands.getElementsByTagName("command");
        elements.counters = (Element) elements.doc.getElementsByTagName("counters").item(0);
        elements.counterNodes = elements.counters.getElementsByTagName("counter");
        elements.filters = (Element) elements.doc.getElementsByTagName("filters").item(0);
        elements.filterNodes = elements.filters.getElementsByTagName("filter");
        elements.phrases = (Element) elements.doc.getElementsByTagName("filterPhrases").item(0);
        elements.phraseNodes = elements.doc.getElementsByTagName("filterPhrase");
        elements.regex = (Element) elements.doc.getElementsByTagName("filterRegexes").item(0);
        elements.regexNodes = elements.doc.getElementsByTagName("filterRegex");
        return elements;
    }
}
