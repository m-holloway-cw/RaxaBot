package com.raxa.store;

//import com.twitchbotx.gui.DashboardController;
//import com.twitchbotx.gui.guiHandler;
//import java.io.BufferedReader;
import com.raxa.bot.utilities.ConfigParameters;
import com.raxa.bot.RaxaBot;
import com.raxa.bot.utilities.Commands;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
//import java.io.PrintStream;
//import java.net.Socket;
import java.util.ArrayList;
//import java.util.Collections;
import java.util.List;
//import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

/**
 * This class deals with all the interactions with the database.
 *
 * Primarily, it deals with fetching of information, and updating the
 * information.
 */
public final class XmlDatastore implements Datastore {

    // store off the elements
    private final ConfigParameters.Elements elements;

    /**
     * Constructor for the XML datastore
     *
     * @param parameters A list of fields in the XML file
     */
    public XmlDatastore(final ConfigParameters.Elements parameters) {
        this.elements = parameters;
    }

    @Override
    public ConfigParameters.Configuration getConfiguration() {
        final ConfigParameters.Configuration configuration = new ConfigParameters.Configuration();
        configuration.account
                = this.elements.configNode.getElementsByTagName("account").item(0).getTextContent();

        configuration.password
                = this.elements.configNode.getElementsByTagName("password").item(0).getTextContent();

        configuration.clientID
                = this.elements.configNode.getElementsByTagName("clientID").item(0).getTextContent();

        configuration.joinedChannel
                = this.elements.configNode.getElementsByTagName("joinedChannel").item(0).getTextContent();

        configuration.host
                = this.elements.configNode.getElementsByTagName("host").item(0).getTextContent();

        configuration.port
                = Integer.parseInt(this.elements.configNode.getElementsByTagName("port").item(0).getTextContent());

        configuration.pubSub = this.elements.configNode.getElementsByTagName("pubSub").item(0).getTextContent();

        configuration.streamerStatus = this.elements.configNode.getElementsByTagName("streamerStatus").item(0).getTextContent();

        configuration.followage = this.elements.configNode.getElementsByTagName("followage").item(0).getTextContent();

        configuration.youtubeApi = this.elements.configNode.getElementsByTagName("youtubeAPI").item(0).getTextContent();

        configuration.youtubeTitle = this.elements.configNode.getElementsByTagName("youtubeTitle").item(0).getTextContent();

        configuration.onlineCheckTimer = Integer.parseInt(this.elements.configNode.getElementsByTagName("onlineCheckTimer").item(0).getTextContent());

        configuration.recentMessageCacheSize = Integer.parseInt(this.elements.configNode.getElementsByTagName("recentMessageCacheSize").item(0).getTextContent());

        configuration.numCounters = Integer.parseInt(this.elements.configNode.getElementsByTagName("numberOfCounters").item(0).getTextContent());

        configuration.pyramidResponse = this.elements.configNode.getElementsByTagName("pyramidResponse").item(0).getTextContent();

        configuration.sqlURL = this.elements.configNode.getElementsByTagName("sqlURL").item(0).getTextContent();
        
        configuration.sqlMURL = this.elements.configNode.getElementsByTagName("sqlMURL").item(0).getTextContent();

        configuration.sqlUser = this.elements.configNode.getElementsByTagName("sqlUser").item(0).getTextContent();

        configuration.sqlPass = this.elements.configNode.getElementsByTagName("sqlPass").item(0).getTextContent();

        configuration.ftpURL = this.elements.configNode.getElementsByTagName("ftpURL").item(0).getTextContent();
        
        configuration.ftpUser = this.elements.configNode.getElementsByTagName("ftpUser").item(0).getTextContent();
        
        configuration.ftpPass = this.elements.configNode.getElementsByTagName("ftpPass").item(0).getTextContent();
        
        configuration.channelID = this.elements.configNode.getElementsByTagName("channelID").item(0).getTextContent();

        configuration.botID = this.elements.configNode.getElementsByTagName("botID").item(0).getTextContent();

        configuration.pubSubAuthToken = this.elements.configNode.getElementsByTagName("pubSubAuthToken").item(0).getTextContent();

        configuration.botWhisperToken = this.elements.configNode.getElementsByTagName("botWhisperToken").item(0).getTextContent();

        configuration.streamlabsToken = this.elements.configNode.getElementsByTagName("streamlabsToken").item(0).getTextContent();
        
        configuration.discordToken = this.elements.configNode.getElementsByTagName("discordToken").item(0).getTextContent();
        
        configuration.spotifyUrl = this.elements.configNode.getElementsByTagName("spotifyUrl").item(0).getTextContent();
        
        configuration.spotifyId = this.elements.configNode.getElementsByTagName("spotifyId").item(0).getTextContent();
        
        configuration.spotifySecret = this.elements.configNode.getElementsByTagName("spotifySecret").item(0).getTextContent();
        
        configuration.spotifyStatus = this.elements.configNode.getElementsByTagName("spotifyStatus").item(0).getTextContent();
        
        configuration.lottoStatus = this.elements.configNode.getElementsByTagName("lottoStatus").item(0).getTextContent();
             
        configuration.rpgStatus = this.elements.configNode.getElementsByTagName("rpgStatus").item(0).getTextContent();
        
        return configuration;
    }

    @Override
    public List<ConfigParameters.Editor> getEditors() {
        final List<ConfigParameters.Editor> editors = new ArrayList<>();
        for (int i = 0; i < this.elements.editorNodes.getLength(); i++) {
            Node n = this.elements.editorNodes.item(i);
            Element e = (Element) n;
            final ConfigParameters.Editor editor = new ConfigParameters.Editor();
            editor.level = Integer.parseInt(e.getAttribute("level"));
            editor.username = e.getAttribute("name");
            editors.add(editor);
        }
        return editors;
    }

    @Override
    public List<ConfigParameters.Command> getCommands() {
        final List<ConfigParameters.Command> commands = new ArrayList<>();

        for (int i = 0; i < this.elements.commandNodes.getLength(); i++) {
            Node n = this.elements.commandNodes.item(i);
            Element e = (Element) n;

            final ConfigParameters.Command command = new ConfigParameters.Command();
            command.auth = e.getAttribute("auth");
            command.name = e.getAttribute("name");
            command.disabled = Boolean.parseBoolean(e.getAttribute("disabled"));
            command.text = e.getTextContent();
            command.cdUntil = e.getAttribute("cdUntil");
            command.initialDelay = e.getAttribute("initialDelay");
            command.interval = e.getAttribute("interval");
            command.repeating = e.getAttribute("repeating");
            command.cooldown = e.getAttribute("cooldown");
            command.sound = e.getAttribute("sound");
            commands.add(command);
        }
        return commands;
    }

    @Override
    public List<ConfigParameters.Filter> getFilters() {
        final List<ConfigParameters.Filter> filters = new ArrayList<>();
        for (int i = 0; i < this.elements.filterNodes.getLength(); i++) {
            Node n = this.elements.filterNodes.item(i);
            Element e = (Element) n;

            final ConfigParameters.Filter filter = new ConfigParameters.Filter();
            filter.name = e.getAttribute("name");
            filter.reason = e.getAttribute("reason");
            filter.enabled = Boolean.parseBoolean(e.getAttribute("enabled"));
            filter.seconds = e.getAttribute("seconds");
            filters.add(filter);
        }

        return filters;
    }

    @Override
    public List<ConfigParameters.FilterRegex> getRegexes() {
        final List<ConfigParameters.FilterRegex> filters = new ArrayList<>();
        for (int i = 0; i < this.elements.regexNodes.getLength(); i++) {
            Node n = this.elements.regexNodes.item(i);
            Element e = (Element) n;

            final ConfigParameters.FilterRegex filter = new ConfigParameters.FilterRegex();
            filter.name = e.getAttribute("name");
            filter.content = e.getAttribute("content");
            filter.reason = e.getAttribute("reason");
            filter.enabled = Boolean.parseBoolean(e.getAttribute("enabled"));
            filter.seconds = e.getAttribute("seconds");
            filters.add(filter);
        }
        return filters;
    }

    @Override
    public List<ConfigParameters.FilterPhrase> getFilterPhrases() {
        final List<ConfigParameters.FilterPhrase> filters = new ArrayList<>();
        for (int i = 0; i < this.elements.phraseNodes.getLength(); i++) {
            Node n = this.elements.phraseNodes.item(i);
            Element e = (Element) n;

            final ConfigParameters.FilterPhrase filter = new ConfigParameters.FilterPhrase();
            filter.phrase = e.getAttribute("phrase");
            filter.reason = e.getAttribute("reason");
            filter.enabled = Boolean.parseBoolean(e.getAttribute("enabled"));
            filter.seconds = e.getAttribute("seconds");
            filters.add(filter);
        }

        return filters;
    }

    @Override
    public List<ConfigParameters.Counter> getCounters() {
        List<ConfigParameters.Counter> counters = new ArrayList<>();
        for (int i = 0; i < this.elements.counterNodes.getLength(); i++) {
            Node n = this.elements.commandNodes.item(i);
            Element e = (Element) n;

            final ConfigParameters.Counter counter = new ConfigParameters.Counter();
            counter.name = e.getAttribute("name");
            counter.count = Integer.parseInt(e.getAttribute("count"));
            counters.add(counter);
        }

        return counters;
    }

    //Keep the bot instance here, get/set methods through RaxaBot 
    private RaxaBot bot;

    @Override
    public RaxaBot getBot() {
        return bot;
    }

    @Override
    public void setBot(RaxaBot sentBot) {
        bot = sentBot;
    }

    
    @Override
    public void modifyConfiguration(final String node, final String value) {
        final Node n = this.elements.configNode.getElementsByTagName(node).item(0);
        final Element el = (Element) n;
        el.setTextContent(value);
        commit();
    }

    //GUI add command with parameter options
    @Override
    public boolean addCommand(final String command, final String auth,
            final String cooldown, final String repeating,
            final String initDelay, final String interval,
            final String sound, final String msg) {
        for (int i = 0; i < this.elements.commandNodes.getLength(); i++) {
            final Node n = this.elements.commandNodes.item(i);
            final Element e = (Element) n;
            if (command.equals(e.getAttribute("name"))) {
                return false;
            }
        }
        Element newNode = this.elements.doc.createElement("command");
        newNode.appendChild(this.elements.doc.createTextNode(msg));
        newNode.setAttribute("name", command.toLowerCase());
        newNode.setAttribute("auth", auth + " "); //until fixed, need to add space after auth to correct checkauthorization
        newNode.setAttribute("repeating", repeating);
        newNode.setAttribute("initialDelay", initDelay);
        newNode.setAttribute("interval", interval);
        newNode.setAttribute("cooldown", cooldown);
        newNode.setAttribute("cdUntil", "0");
        newNode.setAttribute("sound", sound);
        newNode.setAttribute("disabled", "false");
        this.elements.commands.appendChild(newNode);
        commit();
        return true;
    }

    //chat add command with default settings
    @Override
    public boolean addCommand(final String command, final String text) {
        for (int i = 0; i < this.elements.commandNodes.getLength(); i++) {
            final Node n = this.elements.commandNodes.item(i);
            final Element e = (Element) n;
            if (command.equals(e.getAttribute("name"))) {
                return false;
            }
        }
        Element newNode = this.elements.doc.createElement("command");
        newNode.appendChild(this.elements.doc.createTextNode(text));
        newNode.setAttribute("name", command.toLowerCase());
        newNode.setAttribute("auth", "");
        newNode.setAttribute("repeating", "false");
        newNode.setAttribute("initialDelay", "0");
        newNode.setAttribute("interval", "0");
        newNode.setAttribute("cooldown", "0");
        newNode.setAttribute("cdUntil", "0");
        newNode.setAttribute("sound", "");
        newNode.setAttribute("disabled", "false");
        this.elements.commands.appendChild(newNode);
        commit();
        return true;
    }

    @Override
    public boolean editCommand(final String command, final String text) {
        for (int i = 0; i < this.elements.commandNodes.getLength(); i++) {
            final Node n = this.elements.commandNodes.item(i);
            final Element e = (Element) n;
            if (command.equals(e.getAttribute("name"))) {
                e.setTextContent(text);
                commit();
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean deleteCommand(final String command) {
        for (int i = 0; i < this.elements.commandNodes.getLength(); i++) {
            final Node n = this.elements.commandNodes.item(i);
            final Element e = (Element) n;
            if (command.equals(e.getAttribute("name"))) {
                this.elements.commands.removeChild(n);
                commit();
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean updateCounter(final String name, final int delta) {
        for (int i = 0; i < this.elements.counterNodes.getLength(); i++) {
            final Node n = this.elements.counterNodes.item(i);
            final Element e = (Element) n;
            if (name.equals(e.getAttribute("name"))) {
                int value = Integer.parseInt(e.getTextContent()) + delta;
                e.setTextContent(Integer.toString(value));
                commit();

                return true;
            }
        }

        return false;
    }

    @Override
    public boolean setCounter(final String name, final int value) {
        for (int i = 0; i < this.elements.counterNodes.getLength(); i++) {
            final Node n = this.elements.counterNodes.item(i);
            final Element e = (Element) n;
            if (name.equals(e.getAttribute("name"))) {
                e.setTextContent(Integer.toString(value));
                commit();

                return true;
            }
        }

        final Element counterNode = this.elements.doc.createElement("counter");
        counterNode.appendChild(this.elements.doc.createTextNode(Integer.toString(value)));
        counterNode.setAttribute("name", name);
        commit();

        return true;
    }

    @Override
    public boolean addCounter(final String name) {
        for (int i = 0; i < this.elements.counterNodes.getLength(); i++) {
            final Node n = this.elements.counterNodes.item(i);
            final Element e = (Element) n;
            if (name.equals(e.getAttribute("name"))) {
                return false;
            }
        }

        final Element newNode = this.elements.doc.createElement("counter");
        newNode.appendChild(this.elements.doc.createTextNode("0"));
        newNode.setAttribute("name", name);

        this.elements.counters.appendChild(newNode);
        commit();

        return true;
    }

    @Override
    public boolean deleteCounter(final String counterName) {
        for (int i = 0; i < this.elements.counterNodes.getLength(); i++) {
            final Node n = this.elements.counterNodes.item(i);
            final Element e = (Element) n;
            if (counterName.equals(e.getAttribute("name"))) {
                this.elements.filters.removeChild(n);
                commit();
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean addFilter(final ConfigParameters.Filter filter) {
        //TODO index filters and/or separate phrases and filters
        for (int i = 0; i < this.elements.filterNodes.getLength(); i++) {
            final Node n = this.elements.filterNodes.item(i);
            final Element e = (Element) n;
            if (filter.name.equals(e.getAttribute("name"))) {
                return false;
            }
        }

        Element newNode = this.elements.doc.createElement("filter");
        newNode.setAttribute("name", filter.name);
        newNode.setAttribute("reason", filter.reason);
        if (filter.enabled) {
            newNode.setAttribute("enabled", "true");
        } else {
            newNode.setAttribute("enabled", "false");
        }
        newNode.setAttribute("seconds", filter.seconds);
        this.elements.filters.appendChild(newNode);
        commit();
        return true;
    }

    @Override
    public boolean updateFilter(final ConfigParameters.Filter filter) {
        for (int i = 0; i < this.elements.filterNodes.getLength(); i++) {
            final Node n = this.elements.filterNodes.item(i);
            final Element e = (Element) n;
            if (filter.name.contentEquals(e.getAttribute("name"))) {
                e.setAttribute("enabled", String.valueOf(filter.enabled));
                e.setAttribute("reason", filter.reason);
                e.setAttribute("seconds", filter.seconds);
                commit();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean deleteFilter(final String filterName) {
        for (int i = 0; i < this.elements.filterNodes.getLength(); i++) {
            final Node n = this.elements.filterNodes.item(i);
            final Element e = (Element) n;
            if (filterName.contentEquals(e.getAttribute("name"))) {
                this.elements.filters.removeChild(n);
                commit();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean addRegex(ConfigParameters.FilterRegex regex) {

        for (int i = 0; i < this.elements.regexNodes.getLength(); i++) {
            final Node n = this.elements.regexNodes.item(i);
            final Element e = (Element) n;
            if (regex.name.contentEquals(e.getAttribute("name"))) {
                // if names match with any other regex's get out
                return false;
            }
        }
        Element newNode = this.elements.doc.createElement("filterRegex");
        newNode.setAttribute("name", regex.name);
        newNode.setAttribute("reason", regex.reason);
        if (regex.enabled) {
            newNode.setAttribute("enabled", "true");
        } else {
            newNode.setAttribute("enabled", "false");
        }
        newNode.setAttribute("seconds", regex.seconds);
        newNode.setAttribute("content", regex.content);
        this.elements.regex.appendChild(newNode);
        commit();
        return true;
    }

    @Override
    public boolean updateRegex(ConfigParameters.FilterRegex regex, String attribute) {

        for (int i = 0; i < this.elements.regexNodes.getLength(); i++) {
            final Node n = this.elements.regexNodes.item(i);
            final Element e = (Element) n;
            if (regex.name.contentEquals(e.getAttribute("name"))) {
                if (attribute.equals("")) {
                    e.setAttribute("content", regex.content);
                    e.setAttribute("seconds", regex.seconds);
                    e.setAttribute("reason", regex.reason);
                    e.setAttribute("enabled", String.valueOf(regex.enabled));
                } else if (attribute.equalsIgnoreCase("content")) {
                    e.setAttribute("content", regex.content);
                } else if (attribute.equalsIgnoreCase("seconds")) {
                    //using "del" as the seconds value denotes using .delete
                    e.setAttribute("seconds", regex.seconds);
                } else if (attribute.equalsIgnoreCase("reason")) {
                    e.setAttribute("reason", regex.reason);
                } else if (attribute.equalsIgnoreCase("enabled")) {
                    e.setAttribute("enabled", String.valueOf(regex.enabled));
                } else {
                    // if attribute is anything that we're not looking for, return false and prompt retry
                    return false;
                }
                commit();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean deleteRegex(String name) {
        for (int i = 0; i < this.elements.regexNodes.getLength(); i++) {
            final Node n = this.elements.regexNodes.item(i);
            final Element e = (Element) n;
            if (name.contentEquals(e.getAttribute("name"))) {
                this.elements.regex.removeChild(n);
                commit();
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void updateCooldownTimer(final String command, long cooldownUntil) {
        System.out.println(command + " " + cooldownUntil);
        for (int i = 0; i < this.elements.commandNodes.getLength(); i++) {
            Node n = this.elements.commandNodes.item(i);
            Element el = (Element) n;
            if (command.equals(el.getAttribute("name"))) {
                el.setAttribute("cdUntil", Long.toString(cooldownUntil));
                //TODO need commit()?                
                //commit();
            }
        }
    }

    @Override
    public void commit() {
        try {
            File configFile = new File("kfbot.xml");
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer();
            DOMSource source = new DOMSource(this.elements.doc);
            StreamResult result = new StreamResult(configFile);
            transformer.transform(source, result);
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean setUserCommandAttribute(final String command,
            final String attribute,
            final String value,
            final boolean allowReservedCmds) {
        if (!allowReservedCmds && Commands.getInstance().isReservedCommand(command)) {
            return false;
        }

        for (int i = 0; i < this.elements.commandNodes.getLength(); i++) {
            Node n = this.elements.commandNodes.item(i);
            Element el = (Element) n;
            if (command.contentEquals(el.getAttribute("name"))) {
                el.setAttribute(attribute, value);
                commit();
                return true;
            }
        }

        return false;
    }
    
    
    /**
     * Break to running store idea for now
     * separate values for cleanliness/rpg game reasons?
     */
    
        
    //UI elements for sync
    private ListView<String> events;

    @Override
    public ListView<String> getLV() {
        return events;
    }

    @Override
    public void setLV(ListView<String> lv) {
        events = lv;
    }

    private eventObList eventList;

    @Override
    public eventObList getEventList() {
        return eventList;
    }

    @Override
    public void setEvent(eventObList e) {
        eventList = e;
    }

    @ThreadSafe
    public static class eventObList {

        @GuardedBy("this")
        private final ListView<String> eventList = new ListView<>();

        @GuardedBy("this")
        private final ObservableList<String> eventObL = FXCollections.observableArrayList();

        public synchronized ObservableList<String> getList() {
            return this.eventObL;
        }

        public synchronized void setEventList() {
            this.eventList.setItems(this.eventObL);
            this.eventList.scrollTo(this.eventObL.size() - 1);
        }

        public synchronized ListView<String> getEventList() {
            return this.eventList;
        }

        public synchronized void addList(String toAdd) {
            this.eventObL.add(toAdd);
        }

        public synchronized void resetList() {
            this.eventObL.clear();
        }
    }

    
    
    @ThreadSafe
    public static class pongHandler {

        @GuardedBy("this")
        private int pongCounter = 1;

        public synchronized int getPong() {
            return pongCounter;
        }

        public synchronized void addPong() {
            pongCounter++;
        }

        public synchronized void resetPong() {
            pongCounter = 0;
        }

    }
}
