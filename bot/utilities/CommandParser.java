package com.raxa.bot.utilities;

import com.raxa.bot.Start;
import com.raxa.bot.utilities.TwitchMessenger;
import com.raxa.bot.handlers.*;
import com.raxa.bot.rpg.Player;
import com.raxa.gui.handlers.DashboardController;
import com.raxa.store.Datastore;

import java.io.PrintStream;
import java.util.Random;
import java.util.logging.Logger;
import java.util.logging.Level;
import javafx.application.Platform;

/**
 * This class is used to parse all commands flowing through it.
 */
public final class CommandParser {

    private static final Logger LOGGER = Logger.getLogger(CommandParser.class.getSimpleName());
    final ConfigParameters configuration = new ConfigParameters();

    // For handling all normal commands
    private final CommandHandler commandOptionsHandler;

    // For moderation filtering options
    private final ModerationHandler moderationHandler;

    // For Twitch statuses
    private final ConnectionServices connectionServices;

    // For counter handling
    private final CountHandler countHandler;

    // For lottery system
    public static LotteryHandler.Lotto lotto = new LotteryHandler.Lotto();
    public static ChatGamesHandler games = new ChatGamesHandler();

    // For handling SQL transactions
    private final sqlHandler sql;

    // For handling displayname capitalization 
    public static String displayName = "";

    // For filter handling
    public static FilterHandler filterHandler;

    private final Datastore store;

    // A simple constructor for this class that takes in the XML elements
    // for quick modification
    public CommandParser(final Datastore store) {

        //TODO match xml file with all commands to ensure proper authorization and directions
        // all the handlers for different messages
        this.commandOptionsHandler = new CommandHandler(store);
        this.connectionServices = new ConnectionServices();
        this.countHandler = new CountHandler(store);
        filterHandler = new FilterHandler(store);
        this.moderationHandler = new ModerationHandler(store);
        this.sql = new sqlHandler(store);
        this.store = store;
    }

    private void sendMessage(final String msg, boolean action) {

        DashboardController.wIRC.sendMessage(msg, action);
    }

    private void sendEditorMessage(String msg) {
        DashboardController.wIRC.sendEditorMessage(msg);
    }

    /**
     * This method will start handling all the commands and delegating it to the
     * proper handlers. Uses XML file to determine requirements for commands.
     * Requirements set by !command-auth Command enabled/disabled by
     * !command-enable
     *
     * @param mod A boolean field which indicates whether this is a mod message.
     *
     * @param sub A boolean field which indicates whether this is a subscriber
     * message.
     *
     * @param trailing The trailing message that accompany the command
     */
    private void handleCommand(final String username, final String userID, final boolean mod, final boolean sub, String trailing, String msgID) {
        if (trailing.contains("")) {
            trailing = trailing.replaceAll("", "");
            trailing = trailing.replaceFirst("ACTION ", "");
        }
        if (trailing.startsWith("!")) {
            String cmd;
            int cmdEnd = trailing.indexOf(" ");
            if (cmdEnd == -1) {
                trailing = trailing.toLowerCase();
                System.out.println("TRAIL: " + trailing);
            } else {
                cmd = trailing.substring(trailing.indexOf("!"), trailing.indexOf(" "));
                System.out.println(cmd + " COMMAND");
            }
        }

        moderationHandler.handleTool(username, trailing, msgID);

        //add check for lottery entrants
        /* if (lotto.getLottoStatus()) {
            String keyword = lotto.getLottoName();
            if (keyword == null) {
                keyword = "";
            }
            if (trailing.startsWith(keyword) && !keyword.equals("")) {
                lotto.addUser(username, sub);
            }
        }*/
        if (!trailing.startsWith("!")) {
            return;
        }

        /* if (trailing.startsWith("!test1")) {
            sendEvent("Test event command");
        }*/
        if (trailing.startsWith("!part")) {
            if (username.equalsIgnoreCase("thundergrub") || username.equalsIgnoreCase("raxa") || username.equalsIgnoreCase(store.getConfiguration().joinedChannel)) {
                sendMessage("bot leaving chat", true);
                DashboardController.wIRC.close();
            }
        }

        if (trailing.startsWith("!uptime")) {
            LOGGER.log(Level.INFO, "{0} {1} {2}", new Object[]{username, mod, sub});
            if (commandOptionsHandler.checkAuthorization("!uptime", username, mod, sub)) {
                sendMessage(connectionServices.uptime(trailing), true);
            }
            return;
        }

        if (trailing.startsWith("!followage")) {
            if (commandOptionsHandler.checkAuthorization("!followage", username, mod, sub)) {
                String user = username.toLowerCase();
                sendMessage(connectionServices.followage(user), true);
            }
            return;
        }

        //begin raffle system commands
        /*if (trailing.startsWith("!lottery-open")) {
            if (commandOptionsHandler.checkAuthorization("!lottery-open", username, mod, sub)) {
                lotto.lottoOpen(trailing);
            }
            return;
        }

        if (trailing.startsWith("!lottery-clear")) {
            if (commandOptionsHandler.checkAuthorization("!lottery-clear", username, mod, sub)) {
                lotto.lottoClear();
            }
            return;
        }

        if (trailing.startsWith("!unlottery")) {
            lotto.leaveLotto(username);
            return;
        }

        if (trailing.startsWith("!draw")) {
            if (commandOptionsHandler.checkAuthorization("!draw", username, mod, sub)) {
                lotto.drawLotto();
            }
            return;
        }

        // begin sql system
        if (trailing.startsWith("!s-game-add")) {
            if (commandOptionsHandler.checkAuthorization("!s-game-add", username, mod, sub)) {
                //add a new game to sql table, default to 0 points
                sql.addGame(trailing);
            }
            return;
        }
        if (trailing.startsWith("!s-game-delete")) {
            if (commandOptionsHandler.checkAuthorization("!s-game-delete", username, mod, sub)) {
                //delete game from sql table, including entire entry(name + point value)
                sql.deleteGame(trailing);
            }
            return;
        }

        if (trailing.startsWith("!s-set-name")) {
            if (commandOptionsHandler.checkAuthorization("!s-set-name", username, mod, sub)) {
                //manual overwrite of game name
                sql.setName(trailing);
            }
            return;
        }

        if (trailing.startsWith("!s-set-points")) {
            if (commandOptionsHandler.checkAuthorization("!s-set-points", username, mod, sub)) {
                //manual overwrite of point value
                sql.setPoints(trailing);
            }
            return;
        }

        if (trailing.startsWith("!s-addPoints") || trailing.startsWith("!s-addpoints")) {
            if (commandOptionsHandler.checkAuthorization("!s-addPoints", username, mod, sub)) {
                sql.addPoints(trailing);
            }
            return;
        }

        if (trailing.startsWith("!points")) {
            if (commandOptionsHandler.checkAuthorization("!points", username, mod, sub)) {
                //if trailing empty put all names + point amount
                //else put game points ie !points Game1
                sql.getPoints(trailing, username);
            }
            return;
        }

        if (trailing.startsWith("!s-status")) {
            if (commandOptionsHandler.checkAuthorization("!s-addPoints", username, mod, sub)) {
                sql.sStatus(trailing);
            }
            return;
        }
         */
//        if (trailing.startsWith("!commands")) {
//            if (commandOptionsHandler.checkAuthorization("!commands", username, mod, sub)) {
//                commandOptionsHandler.commands(username, mod, sub);
//            }
//        }
        if (trailing.startsWith("!command-add")) {
            if (commandOptionsHandler.checkAuthorization("!command-add", username, mod, sub)) {
                sendMessage(commandOptionsHandler.addCommand(trailing), true);
            }
            return;
        }
        if (trailing.startsWith("!command-delete")) {
            if (commandOptionsHandler.checkAuthorization("!command-delete", username, mod, sub)) {
                sendMessage(commandOptionsHandler.deleteCommand(trailing), true);
            }
            return;
        }
        if (trailing.startsWith("!command-edit")) {
            if (commandOptionsHandler.checkAuthorization("!command-edit", username, mod, sub)) {
                sendMessage(commandOptionsHandler.editCommand(trailing), true);
            }
            return;
        }
        if (trailing.startsWith("!command-auth")) {
            if (commandOptionsHandler.checkAuthorization("!command-auth", username, mod, sub)) {
                sendMessage(commandOptionsHandler.authorizeCommand(username, trailing), true);
            }
            return;
        }
        if (trailing.startsWith("!command-enable")) {
            if (commandOptionsHandler.checkAuthorization("!command-enable", username, mod, sub)) {
                sendMessage(commandOptionsHandler.commandEnable(trailing), true);
            }
            return;
        }
        if (trailing.startsWith("!command-disable")) {
            if (commandOptionsHandler.checkAuthorization("!command-disable", username, mod, sub)) {
                sendMessage(commandOptionsHandler.commandDisable(trailing), true);
            }
            return;
        }
        if (trailing.startsWith("!command-sound")) {
            if (commandOptionsHandler.checkAuthorization("!command-sound", username, mod, sub)) {
                sendMessage(commandOptionsHandler.commandSound(trailing), true);
            }
            return;
        }

        if (trailing.startsWith("!filter-all")) {
            if (commandOptionsHandler.checkAuthorization("!filter-all", username, mod, sub)) {
                sendMessage(filterHandler.getAllFilters(trailing, username), true);
            }
        }
        if (trailing.startsWith("!filter-add")) {
            if (commandOptionsHandler.checkAuthorization("!filter-add", username, mod, sub)) {
                sendMessage(filterHandler.addFilter(trailing, username), true);
            }
        }
        if (trailing.startsWith("!filter-delete")) {
            if (commandOptionsHandler.checkAuthorization("!filter-delete", username, mod, sub)) {
                sendMessage(filterHandler.deleteFilter(trailing, username), true);
            }
        }
        /* if (trailing.startsWith("!cnt-add")) {
            if (commandOptionsHandler.checkAuthorization("!cnt-add", username, mod, sub)) {
                sendMessage(countHandler.addCounter(trailing), true);
            }
            return;
        }
        if (trailing.startsWith("!cnt-delete")) {
            if (commandOptionsHandler.checkAuthorization("!cnt-delete", username, mod, sub)) {
                sendMessage(countHandler.deleteCounter(trailing), true);
            }
            return;
        }
        if (trailing.startsWith("!cnt-set")) {
            if (commandOptionsHandler.checkAuthorization("!cnt-set", username, mod, sub)) {
                sendMessage(countHandler.setCounter(trailing), true);
            }
            return;
        }
        if (trailing.startsWith("!cnt-current")) {
            if (commandOptionsHandler.checkAuthorization("!cnt-current", username, mod, sub)) {
                sendMessage(countHandler.getCurrentCount(trailing), true);
            }
            return;
        }
        if (trailing.startsWith("!countadd")) {
            if (commandOptionsHandler.checkAuthorization("!countadd", username, mod, sub)) {
                sendMessage(countHandler.updateCount(trailing), true);
            }
            return;
        }

        if (trailing.startsWith("!totals")) {
            if (commandOptionsHandler.checkAuthorization("!totals", username, mod, sub)) {
                sendMessage(countHandler.totals(), true);
            }
            return;
        }

        if (trailing.startsWith("!beginRPG")) {
            if (!Start.rpg.userExists(Integer.parseInt(userID))) {
                //reply with info message
                //user can pick race/type/etc in later commands
                sendMessage(Start.rpg.handleNewUser(Integer.parseInt(userID), username), true);
            } else {
                sendMessage("@" + username + " already exists in the system", true);
            }
        }
        if (trailing.startsWith("!setType")) {
            if (Start.rpg.getPlayer(Integer.parseInt(userID)).getType().equals("empty")) {
                String type;
                int typeBegin = trailing.indexOf(" ") + 1;
                type = trailing.substring(typeBegin);
                if (Start.rpg.checkType(type)) {
                    Start.rpg.addUser(Integer.parseInt(userID), new Player(username, type));
                } else {
                    sendMessage("@" + username + " type: \'" + type + "\' is not a valid type", true);
                }
            } else {
                sendMessage("@" + username + " type has already been set", true);
            }
        }

        if (trailing.startsWith("!setName")) {
            Player p;
            p = Start.rpg.getPlayer(Integer.parseInt(userID));
            if (p != null) {
                String name;
                int nameBegin = trailing.indexOf(" ") + 1;
                name = trailing.substring(nameBegin); // names are allowed to have spaces
                if (p.setName(name)) {
                    sendMessage("Succesfully set name to \'" + name + "\'", true);
                } else {
                    sendMessage("No player account found for " + username, true);
                }
            }
        }
        //TODO player weapon/armor/etc commands
         */

        if (trailing.startsWith("!roulette")) {
            boolean result = games.roulette();
            if (result) {
                sendMessage("raxaOH " + username + " pulls the trigger. raxaTride", true);
                sendMessage(".timeout " + username + " 1 bewm goes the dynamite.", false);
            } else {
                sendMessage("Click. " + username + " survives this round. raxaPLS", true);
            }
        }

        sendMessage(commandOptionsHandler.parseForUserCommands(trailing, username, mod, sub), true);
    }

    /**
     * This method parses all incoming messages from Twitch IRC in the bots
     * channel.
     *
     * @param user Username of person using the command.
     * @param msg A string that represents the message type.
     * @param channel Channel that the message is coming from.
     */
    public void handleEditorCommand(String user, String msg, String channel) {
        // Check for editor level of user
        int level = 0;
        if (store.getConfiguration().joinedChannel.equalsIgnoreCase(user)) {
            level = 600;
        } else {
            for (int i = 0; i < store.getEditors().size(); i++) {
                final ConfigParameters.Editor editor = store.getEditors().get(i);
                if (editor.username.equalsIgnoreCase(user)) {
                    level = editor.level;
                }
            }
        }
        sendEditorMessage(editorCommandHandler.parseForCommand(user, level, msg, channel));
    }

    public void handleWhisper(String user, String msg) {
        System.out.println("Whisper from " + user + ", message: " + msg);
    }

    /**
     * This method creates a duplicate chat for use with chat module.
     *
     * incomingMessage(displayName, isMod, isSub, color, emotes, msg );
     *
     * @param displayName capital correct version of name
     * @param isMod moderator sword boolean
     * @param isSub sub badge boolean
     * @param color hex for color i.e. #102372
     * @param emotes id code for set of emotes for the user i.e.
     * emotes=;id=10c8f35b-0e91-454b-9aea-8b56e70f75d8;
     * @param msg A string of the message
     *
     */
    private void incomingMessage(String displayName, boolean isMod, boolean isSub, String color, String emotes, String msg, String msgId, boolean coloredMessage) {

    }

    /**
     * This method parses all incoming messages from Twitch IRC.
     *
     * @param msg A string that represents the message type.
     */
    public void parse(String msg) {
        try {
            // If nothing is provided, exit out of here
            if (msg == null || msg.isEmpty()) {
                return;
            }

            //Handle reconnet notice ":tmi.twitch.tv RECONNECT"
            if (msg.equals(":tmi.twitch.tv RECONNECT")) {
                System.out.println("RECONNECT notice received, restarting bot");
                sendEvent("RECONNECT notice received, attempting to reconnect");
                DashboardController.wIRC.close();
            }

            boolean isMod = false;
            boolean isSub = false;
            String username = "";
            String chanFind = msg;
            String sendRaw = msg;
            // This is a message from a user.
            // If it's the broadcaster, he/she is a mod.
            LOGGER.info(msg);
            if (msg.startsWith("@badges=broadcaster/1")) {
                isMod = true;
            }

            // Find the mod indication
            final int modPosition = msg.indexOf("mod=") + 4;
            if ("1".equals(msg.substring(modPosition, modPosition + 1))) {
                isMod = true;
            }

            // Find the VIP indication, treat at sub level
            if (msg.contains("@badges=vip/1,")) {
                isSub = true;
            }
            // Find the subscriber indication
            final int subPosition = msg.indexOf("subscriber=") + 11;
            if ("1".equals(msg.substring(subPosition, subPosition + 1))) {
                isSub = true;
            }
            String userID = "";
            if (msg.contains("user-id=")) {
                userID = msg.substring(msg.indexOf("user-id=") + 8, msg.indexOf(";", msg.indexOf("user-id=")));
            }

            if (msg.contains("user-type=")) {
                int usernameStart = msg.indexOf(":", msg.indexOf("user-type="));
                int usernameEnd = msg.indexOf("!", usernameStart);
                if (usernameStart != -1 && usernameEnd != -1) {
                    username = msg.substring(usernameStart + 1, usernameEnd).toLowerCase();
                }
            }

            // Split the message into pieces to find the real message
            final int msgPosition = msg.indexOf("user-type=");

            // No message to be processed
            if (msgPosition == -1) {
                return;
            }
            msg = msg.substring(msgPosition);

            // Find the # for the channel, so we can figured out what type
            // of message this is.
            int channelPosition = 0;
            if (msg.contains("#")) {
                channelPosition = msg.indexOf("#");
            } else if (msg.contains(".tmi.twitch.tv WHISPER")) {
                channelPosition = 0;
            } else {
                return;
            }
            if (msgPosition == -1) {
                return;
            }

            // Ensure we can find "PRIVMSG" as an indication that this is a
            // user message, make sure we only search a limited bound, because
            // somebody can potentially fake a mod by including "PRIVMSG" 
            // in their message
            if (channelPosition > 0) {

                // Capture the raw message, and find the message used
                final int msgIndex = msg.indexOf(":", channelPosition);

                // No message found, return immediately
                if (msgIndex == -1) {
                    return;
                }

                msg = msg.substring(msgIndex + 1);

                // Catch msg-id for use in the moderation handler for .delete msgID
                final int msgIdBegin = sendRaw.indexOf("id=") + 3;
                final int msgIdEnd = sendRaw.indexOf(";", msgIdBegin);
                String msgId = sendRaw.substring(msgIdBegin, msgIdEnd);

                // Determine where message is from
                final int channelName = chanFind.indexOf("#", chanFind.indexOf("PRIVMSG"));
                final int chanIndex = chanFind.indexOf(" ", channelName);
                String channel = chanFind.substring(channelName + 1, chanIndex);
                String botName = store.getConfiguration().account;

                // filter system access via bot channel's chat
                if (channel.equalsIgnoreCase(botName)) {
                    handleEditorCommand(username, msg, channel);
                } else {

                    // Handle the message
                    handleCommand(username, userID, isMod, isSub, msg, msgId);

                }
            } else {
                //handle an incoming whisper
                int whisperBegin = msg.indexOf("WHISPER") + 17;
                msg = msg.substring(whisperBegin);
                handleWhisper(username, msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.log(Level.WARNING, "Error detected in parsing a message: throwing away message ", e.getMessage());
        }
    }

    //Method to add events to the GUI event list
    //Stored in the store, created in DashboardController
    //to address thread safety and concurrency
    private void sendEvent(final String msg) {
        String event = msg;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                store.getEventList().addList(event);
            }
        });
    }
}
