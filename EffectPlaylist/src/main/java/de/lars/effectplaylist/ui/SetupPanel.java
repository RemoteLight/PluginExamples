package de.lars.effectplaylist.ui;

import de.lars.effectplaylist.EffectPlaylist;
import de.lars.effectplaylist.Playlist;
import de.lars.effectplaylist.PlaylistElement;
import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.ui.components.ListElement;
import de.lars.remotelightclient.ui.panels.tools.ToolsPanel;
import de.lars.remotelightclient.utils.ui.UiUtils;

import javax.swing.*;
import java.awt.*;

public class SetupPanel extends JPanel {

    private final EffectPlaylist instance;
    private final ToolsPanel context;
    private final Playlist playlist;
    private final JPanel panelList;
    private final JPanel panelSettings;

    public SetupPanel(ToolsPanel context, Playlist playlist) {
        this.context = context;
        instance = EffectPlaylist.getInstance();
        if(playlist != null) {
            this.playlist = playlist;
        } else {
            boolean added = false;
            this.playlist = new Playlist("Playlist #" + (int) (Math.random() * 1000.0));
            do {
                added = instance.getHandler().addPlaylist(this.playlist);
                if(!added)
                    this.playlist.setId("Playlist #" + (int) (Math.random() * 1000.0));
            } while (!added);
        }
        setBackground(Style.panelBackground);
        setLayout(new BorderLayout());

        panelList = new JPanel();
        panelList.setBackground(Style.panelDarkBackground);
        panelList.setLayout(new BoxLayout(panelList, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(panelList);
        scrollPane.setViewportBorder(null);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollPane.getVerticalScrollBar().setUnitIncrement(8);
        add(scrollPane, BorderLayout.CENTER);

        panelSettings = new JPanel();
        panelSettings.setBackground(Style.panelDarkBackground);
        panelSettings.setLayout(new BoxLayout(panelSettings, BoxLayout.Y_AXIS));
        add(panelSettings, BorderLayout.EAST);

        initSettingsPanel();
        updateList();
    }

    public void updateList() {
        for(PlaylistElement item : playlist.getPlaylistList()) {
            ListElement el = new ListElement();

            JLabel lblName = new JLabel(item.getName());
            lblName.setForeground(Style.textColor);
            el.add(lblName);
            el.add(Box.createHorizontalStrut(5));

            JLabel lblType = new JLabel(item.getType().name());
            lblType.setForeground(Style.textColorDarker);
            el.add(lblType);
            el.add(Box.createHorizontalGlue());

            JButton btnMoveUp = new JButton("Up");
            PlaylistPanel.configureBorderlessButton(btnMoveUp);
            btnMoveUp.addActionListener(e -> {
                // TODO move item up
            });
            el.add(btnMoveUp);

            JButton btnMoveDown = new JButton("Down");
            PlaylistPanel.configureBorderlessButton(btnMoveDown);
            btnMoveDown.addActionListener(e -> {
                // TODO move item down
            });
            el.add(btnMoveDown);

            JButton btnRemove = new JButton("Remove");
            PlaylistPanel.configureBorderlessButton(btnRemove);
            btnRemove.addActionListener(e -> {
                // TODO remove from playlist
            });
            el.add(btnRemove);

            panelList.add(el);
            panelList.add(Box.createVerticalStrut(5));
        }
    }

    private void initSettingsPanel() {
        JTextField fieldName = new JTextField(playlist.getId());

        JButton btnAdd = new JButton("Add");
        UiUtils.configureButton(btnAdd);
        btnAdd.addActionListener(e -> {
            // TODO add element
        });
        panelSettings.add(btnAdd);

        JButton btnClear = new JButton("Clear");
        UiUtils.configureButton(btnClear);
        btnClear.addActionListener(e -> {
            // clear playlist
            playlist.getPlaylistList().clear();
            updateList();
        });
        panelSettings.add(btnClear);

        JCheckBox checkLoop = new JCheckBox("Loop");
        checkLoop.setBackground(panelSettings.getBackground());
        checkLoop.setForeground(Style.textColor);
        checkLoop.setSelected(playlist.isLoop());
        panelSettings.add(checkLoop);

        panelSettings.add(Box.createVerticalGlue());

        JButton btnSave = new JButton("Save playlist");
        UiUtils.configureButtonWithBorder(btnSave, Style.accent);
        btnSave.addActionListener(e -> {
            // TODO save and go back
            playlist.setLoop(checkLoop.isSelected());
            String newName = fieldName.getText().trim();
            if(!playlist.getId().equals(newName)) {
                // check if id is not used
                if(instance.getHandler().getPlaylist(newName) != null) {
                    JOptionPane.showInternalMessageDialog(this,
                            "The name is already in use. Please define a unique one.",
                            "Could not save playlist",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // save new name
                playlist.setId(newName);
            }
            // go back
            context.navigateDown();
        });
        panelSettings.add(btnSave);

        JButton btnDelete = new JButton("Delete playlist");
        UiUtils.configureButton(btnDelete);
        btnDelete.addActionListener(e -> {
            // delete playlist
            instance.getHandler().removePlaylist(playlist);
            context.navigateDown();
        });
        panelSettings.add(btnDelete);
    }

}
