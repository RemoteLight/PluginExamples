package de.lars.effectplaylist.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.CompoundBorder;

import de.lars.effectplaylist.EffectPlaylist;
import de.lars.effectplaylist.Playlist;
import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.ui.components.ListElement;
import de.lars.remotelightclient.ui.panels.tools.ToolsPanel;
import de.lars.remotelightclient.ui.panels.tools.ToolsPanelNavItem;
import de.lars.remotelightclient.utils.ui.MenuIconFont;
import de.lars.remotelightplugincompat.StyleCompat;

public class PlaylistPanel extends JPanel {

    private final ToolsPanel context;
    private final EffectPlaylist instance;
    private final JPanel panelPlaylistList;

    public PlaylistPanel(ToolsPanel context, EffectPlaylist instance) {
        this.context = context;
        this.instance = instance;
        setBackground(StyleCompat.panelBackground());
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panelPlaylistList = new JPanel();
        panelPlaylistList.setBackground(StyleCompat.panelDarkBackground());
        panelPlaylistList.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panelPlaylistList.setLayout(new BoxLayout(panelPlaylistList, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(panelPlaylistList);
        scrollPane.setViewportBorder(null);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollPane.getVerticalScrollBar().setUnitIncrement(8);
        add(scrollPane, BorderLayout.CENTER);

        updatePlaylistEntryPanels();
    }

    public void updatePlaylistEntryPanels() {
        panelPlaylistList.removeAll();
        for(final Playlist playlist : instance.getHandler().getAllPlaylists()) {
            ListElement el = new ListElement();

            boolean activePlaylist = instance.getHandler().isActive() && instance.getHandler().getActivePlaylist().getId().equals(playlist.getId());
            if(activePlaylist) {
                el.setBorder(new CompoundBorder(BorderFactory.createLineBorder(StyleCompat.accent()), el.getBorder()));
            }

            JLabel lblName = new JLabel(playlist.getId());
            lblName.setForeground(StyleCompat.textColor());
            el.add(lblName);
            el.add(Box.createHorizontalGlue());

            JButton btnEdit = new JButton("Edit");
            configureBorderlessButton(btnEdit);
            btnEdit.addActionListener(e -> editPlaylist(playlist));
            el.add(btnEdit);

            JButton btnStart = new JButton(activePlaylist ? "Stop" : "Start");
            configureBorderlessButton(btnStart);
            btnStart.addActionListener(e -> {
                if(activePlaylist) {
                    // stop playlist
                    instance.getHandler().stopPlaylist();
                } else {
                    // start playlist
                    instance.getHandler().startPlaylist(playlist);
                }
                updatePlaylistEntryPanels();
            });
            el.add(btnStart);

            panelPlaylistList.add(el);
            panelPlaylistList.add(Box.createVerticalStrut(5));
        }

        // 'Add' button
        ListElement elAdd = new ListElement();
        elAdd.add(new JLabel(Style.getFontIcon(MenuIconFont.MenuIcon.ADD)));
        elAdd.add(Box.createHorizontalStrut(5));

        JLabel lblAdd = new JLabel("Add playlist");
        lblAdd.setForeground(StyleCompat.textColor());
        elAdd.add(lblAdd);

        elAdd.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                editPlaylist(null);
            }
        });
        panelPlaylistList.add(elAdd);
        panelPlaylistList.updateUI();
    }

    /**
     * Open playlist edit panel
     * @param playlist  the playlist to edit (set to null to create a new playlist)
     */
    private void editPlaylist(Playlist playlist) {
        SetupPanel setupPanel = new SetupPanel(context, playlist, this);
        ToolsPanelNavItem navItem = new ToolsPanelNavItem("Playlist Configuration", setupPanel, setupPanel);

        context.navigateUp(navItem);
    }

    public static void configureBorderlessButton(JButton btn) {
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setFocusable(true);
        btn.setOpaque(true);
        btn.setBackground(null);
        btn.setForeground(StyleCompat.textColor());
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

}
