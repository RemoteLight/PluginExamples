package de.lars.effectplaylist.ui;

import de.lars.effectplaylist.EffectPlaylist;
import de.lars.effectplaylist.Playlist;
import de.lars.effectplaylist.PlaylistElement;
import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.ui.components.ListElement;
import de.lars.remotelightclient.ui.panels.tools.ToolsNavListener;
import de.lars.remotelightclient.ui.panels.tools.ToolsPanel;
import de.lars.remotelightclient.utils.ui.UiUtils;
import de.lars.remotelightcore.animation.Animation;
import de.lars.remotelightcore.animation.AnimationManager;
import de.lars.remotelightcore.musicsync.MusicEffect;
import de.lars.remotelightcore.musicsync.MusicSyncManager;

import javax.swing.*;
import java.awt.*;

public class SetupPanel extends JPanel implements ToolsNavListener {

    private final EffectPlaylist instance;
    private final ToolsPanel context;
    private final Playlist playlist;
    private final PlaylistPanel playlistPanel;
    private final JPanel panelList;
    private final JPanel panelSettings;

    public SetupPanel(ToolsPanel context, Playlist playlist, PlaylistPanel playlistPanel) {
        this.context = context;
        this.playlistPanel = playlistPanel;
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
        panelList.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panelList.setLayout(new BoxLayout(panelList, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(panelList);
        scrollPane.setViewportBorder(null);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollPane.getVerticalScrollBar().setUnitIncrement(8);
        add(scrollPane, BorderLayout.CENTER);

        panelSettings = new JPanel();
        panelSettings.setBackground(Style.panelBackground);
        panelSettings.setLayout(new BoxLayout(panelSettings, BoxLayout.Y_AXIS));
        add(panelSettings, BorderLayout.EAST);

        initSettingsPanel();
        updateList();
    }

    /**
     * Update the list of playlist elements
     */
    public void updateList() {
        panelList.removeAll();
        for(PlaylistElement item : playlist.getPlaylistList()) {
            ListElement el = new ListElement();

            JLabel lblName = new JLabel(item.getName());
            lblName.setForeground(Style.textColor);
            el.add(lblName);
            el.add(Box.createHorizontalStrut(5));

            JLabel lblType = new JLabel(item.getType().name());
            lblType.setForeground(Style.textColorDarker);
            el.add(lblType);
            el.add(Box.createHorizontalStrut(5));

            JLabel lblDuration = new JLabel("(" + item.getDuration() + " sec)");
            lblDuration.setForeground(Style.textColorDarker);
            el.add(lblDuration);
            el.add(Box.createHorizontalGlue());

            JButton btnMoveUp = new JButton("Up");
            PlaylistPanel.configureBorderlessButton(btnMoveUp);
            btnMoveUp.addActionListener(e -> {
                // TODO move item up
                int oldIndex = playlist.getPlaylistList().indexOf(item);
                if(oldIndex > 0) {
                    playlist.getPlaylistList().remove(item);
                    playlist.getPlaylistList().add(oldIndex-1, item);
                    updateList();
                }
            });
            el.add(btnMoveUp);

            JButton btnMoveDown = new JButton("Down");
            PlaylistPanel.configureBorderlessButton(btnMoveDown);
            btnMoveDown.addActionListener(e -> {
                // move item down
                int oldIndex = playlist.getPlaylistList().indexOf(item);
                if(oldIndex != -1 && playlist.getPlaylistList().size() > oldIndex+1) {
                    // move element after the target element up
                    PlaylistElement after = playlist.getPlaylistList().remove(oldIndex+1);
                    playlist.getPlaylistList().add(oldIndex, after);
                    updateList();
                }
            });
            el.add(btnMoveDown);

            JButton btnRemove = new JButton("Remove");
            PlaylistPanel.configureBorderlessButton(btnRemove);
            btnRemove.addActionListener(e -> {
                // remove from playlist
                playlist.getPlaylistList().remove(item);
                updateList();
            });
            el.add(btnRemove);

            panelList.add(el);
            panelList.add(Box.createVerticalStrut(5));
        }
        panelList.updateUI();
    }

    private void initSettingsPanel() {
        final int height = 35;
        final Dimension max = new Dimension(Integer.MAX_VALUE, height);
        final Dimension min = new Dimension(140, height);

        JTextField fieldName = new JTextField(playlist.getId());
        fieldName.setMaximumSize(max);
        fieldName.setPreferredSize(new Dimension(120, height));
        fieldName.setAlignmentX(Component.RIGHT_ALIGNMENT);
        panelSettings.add(fieldName);

        JButton btnAdd = new JButton("Add Effect");
        UiUtils.configureButton(btnAdd);
        btnAdd.setMaximumSize(max);
        btnAdd.setPreferredSize(min);
        btnAdd.setAlignmentX(Component.RIGHT_ALIGNMENT);
        btnAdd.addActionListener(e -> {
            // add new playlist element
            PlaylistElement element = openAddDialog();
            if(element != null) {
                playlist.getPlaylistList().add(element);
                updateList();
            }
        });
        panelSettings.add(btnAdd);

        JButton btnClear = new JButton("Clear");
        UiUtils.configureButton(btnClear);
        btnClear.setMaximumSize(max);
        btnClear.setPreferredSize(min);
        btnClear.setAlignmentX(Component.RIGHT_ALIGNMENT);
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
        checkLoop.setMaximumSize(max);
        checkLoop.setAlignmentX(Component.RIGHT_ALIGNMENT);
        panelSettings.add(checkLoop);

        panelSettings.add(Box.createVerticalGlue());

        JButton btnSave = new JButton("Save playlist");
        UiUtils.configureButton(btnSave);
        btnSave.setMaximumSize(max);
        btnSave.setPreferredSize(min);
        btnSave.setAlignmentX(Component.RIGHT_ALIGNMENT);
        btnSave.addActionListener(e -> {
            // save and go back
            playlist.setLoop(checkLoop.isSelected());
            String newName = fieldName.getText().trim();
            if(!playlist.getId().equals(newName)) {
                // check if id is not used
                if(instance.getHandler().getPlaylist(newName) != null) {
                    JOptionPane.showMessageDialog(this,
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
        btnDelete.setMaximumSize(max);
        btnDelete.setPreferredSize(min);
        btnDelete.setAlignmentX(Component.RIGHT_ALIGNMENT);
        btnDelete.addActionListener(e -> {
            // delete playlist
            instance.getHandler().removePlaylist(playlist);
            context.navigateDown();
        });
        panelSettings.add(btnDelete);
    }

    /** last selected effect type */
    private static PlaylistElement.Type lastSelType = PlaylistElement.Type.Animation;

    /**
     * Open dialog to select effect
     * @return  {@link PlaylistElement} or null
     */
    private PlaylistElement openAddDialog() {
        AnimationManager aniManager = instance.getInterface().getAnimationManager();
        MusicSyncManager musicManager = instance.getInterface().getMusicSyncManager();
        String[] animations = aniManager.getAnimations().stream().map(Animation::getName).toArray(String[]::new);
        String[] musicEffects = musicManager.getMusicEffects().stream().map(MusicEffect::getName).toArray(String[]::new);

        JPanel root = new JPanel();
        root.setBackground(Style.panelBackground);
        root.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 5, 5, 5);
        c.gridx = 0;
        c.gridy = 0;

        JComboBox<String> comboEffects = new JComboBox<>();
        comboEffects.setModel(new DefaultComboBoxModel<>(animations));
        comboEffects.setMaximumRowCount(10);

        JLabel lblType = new JLabel("Effect type");
        lblType.setForeground(Style.textColor);
        root.add(lblType, c);

        JComboBox<PlaylistElement.Type> comboType = new JComboBox<>();
        comboType.setModel(new DefaultComboBoxModel<>(PlaylistElement.Type.values()));
        comboType.addActionListener(e -> {
            PlaylistElement.Type sel = (PlaylistElement.Type) comboType.getSelectedItem();
            comboEffects.setModel(new DefaultComboBoxModel<>(sel == PlaylistElement.Type.Animation ? animations : musicEffects));
            lastSelType = sel;
        });
        comboType.setSelectedItem(lastSelType);
        c.gridx = 1;
        c.gridy = 0;
        root.add(comboType, c);

        JLabel lblEffect = new JLabel("Effect");
        lblEffect.setForeground(Style.textColor);
        c.gridx = 0;
        c.gridy = 1;
        root.add(lblEffect, c);

        c.gridx = 1;
        c.gridy = 1;
        root.add(comboEffects, c);

        JLabel lblDuration = new JLabel("Duration (sec)");
        lblDuration.setForeground(Style.textColor);
        c.gridx = 0;
        c.gridy = 2;
        root.add(lblDuration, c);

        JSpinner spinnerDuration = new JSpinner(new SpinnerNumberModel(30, 2, 60*10, 5));
        c.gridx = 1;
        c.gridy = 2;
        root.add(spinnerDuration, c);

        // create dialog
        Object tmpBgr = UIManager.get("Panel.background"); // save default UI value
        UIManager.put("Panel.background", Style.panelBackground); // set custom panel background
        JOptionPane pane = new JOptionPane(root, JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new String[] {"Select"});
        pane.setBackground(Style.panelBackground);
        JDialog dialog = pane.createDialog("Add new playlist item");
        UIManager.put("Panel.background", tmpBgr); // reset to default value
        dialog.setVisible(true);
        dialog.dispose();

        // return selection as playlist element
        PlaylistElement.Type type = (PlaylistElement.Type) comboType.getSelectedItem();
        String name = (String) comboEffects.getSelectedItem();
        int duration = (int) spinnerDuration.getValue();
        return new PlaylistElement(type, name, duration);
    }

    @Override
    public void onBack() {
        // update playlist panel
        playlistPanel.updatePlaylistEntryPanels();
    }

    @Override
    public void onShow() {}
}
