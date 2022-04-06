package com.raxa.bot.utilities;

import com.raxa.bot.Start;
import com.raxa.gui.handlers.DashboardController;
import java.io.PrintStream;

/**
 * This is a Twitch messenger for all outbound twitch messages.
 */
public final class TwitchMessenger {

    private final PrintStream outstream;

    private final String channel;

    public TwitchMessenger(final PrintStream stream, final String channel) {
        this.outstream = stream;
        this.channel = channel;
    }

    /**
     * This method will send a whisper out to a particular user. The user should
     * be include as part of the message.
     *
     * @param msg The message to be sent out to the channel.
     */
    private void sendWhisper(final String msg) {
        DashboardController.wIRC.sendMessage(msg, false);
    }

    /**
     * This command will send a message out to a specific Twitch channel.
     *
     * It will also wrap the message in pretty text (> /me) before sending it
     * out.
     *
     * @param msg The message to be sent out to the channel
     */
    private void sendMessage(final String msg) {
        DashboardController.wIRC.sendMessage(msg, true);
    }

    /**
     * This command will send a message out to the bot Twitch channel.
     *
     *
     * @param msg The message to be sent out to the channel
     */
    public void sendEditorMessage(final String msg) {
        DashboardController.wIRC.sendEditorMessage(msg);
    }
}
