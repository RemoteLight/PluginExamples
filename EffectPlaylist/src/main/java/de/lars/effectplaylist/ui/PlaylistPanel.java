package de.lars.effectplaylist.ui;

import de.lars.effectplaylist.EffectPlaylist;
import de.lars.effectplaylist.Playlist;
import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.ui.components.ListElement;
import de.lars.remotelightclient.ui.panels.tools.ToolsPanelNavItem;
import de.lars.remotelightclient.utils.ui.MenuIconFont;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PlaylistPanel extends JPanel {

    private  EffectPlaylist context;
    private final JPanel panelPlaylistList;

    public PlaylistPanel(EffectPlaylist context) {
        this.context = context;
        setBackground(Style.panelBackground);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panelPlaylistList = new JPanel();
        panelPlaylistList.setBackground(Style.panelDarkBackground);
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
        for(final Playlist playlist : context.getHandler().getAllPlaylists()) {
            ListElement el = new ListElement();

            boolean activePlaylist = context.getHandler().getActivePlaylist().getId().equals(playlist.getId());
            if(activePlaylist) {
                el.setBorder(new CompoundBorder(el.getBorder(), BorderFactory.createLineBorder(Style.accent)));
            }

            JLabel lblName = new JLabel(playlist.getId());
            lblName.setForeground(Style.textColor);
            panelPlaylistList.add(lblName);
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
                    context.getHandler().stopPlaylist();
                } else {
                    // start playlist
                    context.getHandler().startPlaylist(playlist);
                }
            });
            el.add(btnStart);

            panelPlaylistList.add(el);
            panelPlaylistList.add(Box.createVerticalStrut(10));
        }

        panelPlaylistList.add(Box.createVerticalGlue());

        // 'Add' button
        ListElement elAdd = new ListElement();
        elAdd.add(new JLabel(Style.getFontIcon(MenuIconFont.MenuIcon.ADD)));
        elAdd.add(new JLabel("Add playlist"));
        elAdd.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                editPlaylist(null);
            }
        });
        panelPlaylistList.add(elAdd);
    }

    /**
     * Open playlist edit panel
     * @param playlist  the playlist to edit (set to null to create a new playlist)
     */
    private void editPlaylist(Playlist playlist) {
        SetupPanel setupPanel = new SetupPanel(playlist);
        ToolsPanelNavItem navItem = new ToolsPanelNavItem("Playlist Configuration", setupPanel);
        // TODO navigate up in tools panel
    }

    private void configureBorderlessButton(JButton btn) {
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setFocusable(true);
        btn.setOpaque(true);
        btn.setBackground(null);
        btn.setForeground(Style.textColor);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

}
