package iqq.app.ui.frame.panel.chat;

import com.alee.extended.image.WebDecoratedImage;
import com.alee.extended.painter.ColorPainter;
import com.alee.extended.panel.CenterPanel;
import com.alee.extended.panel.GroupPanel;
import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.toolbar.ToolbarStyle;
import com.alee.laf.toolbar.WebToolBar;
import iqq.api.bean.IMEntity;
import iqq.api.bean.IMMsg;
import iqq.api.bean.IMUser;
import iqq.app.core.service.SkinService;
import iqq.app.ui.IMPanel;
import iqq.app.ui.frame.panel.chat.msg.MsgGroupPanel;
import iqq.app.ui.frame.panel.chat.msg.MsgPane;
import iqq.app.ui.frame.panel.chat.rich.RichTextPane;
import iqq.app.ui.frame.panel.chat.rich.UIRichItem;
import iqq.app.ui.frame.panel.chat.rich.UITextItem;
import iqq.app.util.UIUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.*;
import java.util.List;

/**
 * Project  : iqq-projects
 * Author   : 承∮诺 < 6208317@qq.com >
 * Created  : 14-5-11
 * License  : Apache License 2.0
 */
public abstract class EntityPanel extends IMPanel {
    /**
     * 面板聊天对象
     */
    protected IMEntity entity;

    /**
     * 输入面板
     */
    protected RichTextPane contentInput = new RichTextPane();
    /**
     * 输入面板工具条
     */
    protected WebToolBar inputToolbar = new WebToolBar();


    /**
     * 昵称
     */
    protected WebLabel nickLabel = new WebLabel();
    /**
     * 签名
     */
    protected WebLabel signLabel = new WebLabel();

    /**
     * 头像
     */
    protected WebDecoratedImage avatarImage = new WebDecoratedImage();

    protected WebButton textBtn = new WebButton();
    protected WebButton emoticonBtn = new WebButton();
    protected WebButton screenCaptureBtn = new WebButton();
    protected WebButton shakeBtn = new WebButton();
    protected WebButton picturesBtn = new WebButton();
    protected WebButton historyBtn = new WebButton();
    protected WebButton sendBtn = new WebButton();

    protected IMPanel headerPanel = new IMPanel();
    protected MsgGroupPanel msgGroupPanel = new MsgGroupPanel();
    protected IMPanel inputPanel = new IMPanel();
    protected boolean isAppendMsg = false;

    public EntityPanel(IMEntity entity) {
        this.entity = entity;

        createHeader();
        createContent();
        createInput();

        update();
    }

    public IMEntity getEntity() {
        return entity;
    }

    public void setEntity(IMEntity entity) {
        this.entity = entity;
    }

    private void createHeader() {
        avatarImage.setShadeWidth(1);
        avatarImage.setRound(4);
        avatarImage.setDrawGlassLayer(false);

        GroupPanel textGroup = new GroupPanel(0, false, nickLabel, signLabel);
        textGroup.setMargin(0, 10, 0, 5);

        headerPanel.add(avatarImage, BorderLayout.WEST);
        headerPanel.add(new CenterPanel(textGroup, false , true), BorderLayout.CENTER);
        headerPanel.setMargin(8);
        add(headerPanel, BorderLayout.NORTH);
    }

    protected void createContent() {
        msgGroupPanel.setOpaque(false);
        WebScrollPane msgScroll = new WebScrollPane(msgGroupPanel) {
            {
                setHorizontalScrollBarPolicy(WebScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                setBorder(null);
                setMargin(0);
                setShadeWidth(0);
                setRound(0);
                setDrawBorder(false);
                setOpaque(false);
            }
        };

        // 获取JScrollPane中的纵向JScrollBar
        JScrollBar bar = msgScroll.getVerticalScrollBar();
        bar.setUnitIncrement(30);
        bar.addAdjustmentListener(new AdjustmentListener() {

            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                if (isAppendMsg) {
                    e.getAdjustable().setValue(e.getAdjustable().getMaximum());
                    isAppendMsg = false;
                }
            }
        });
        add(msgScroll, BorderLayout.CENTER);
    }

    protected void createInput() {
        initInputToolbar();
        initInputToolbarListener();
        contentInput.setPreferredSize(new Dimension(-1, 80));
        inputPanel.add(inputToolbar, BorderLayout.NORTH);
        inputPanel.add(contentInput, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);
    }

    private void initInputToolbar() {
        setInputButtonStyle(textBtn);
        setInputButtonStyle(emoticonBtn);
        setInputButtonStyle(screenCaptureBtn);
        setInputButtonStyle(shakeBtn);
        setInputButtonStyle(picturesBtn);
        setInputButtonStyle(historyBtn);
        setInputButtonStyle(sendBtn);

        // add to Toolbar
        inputToolbar.addSpacing(10);
        // toolbar.add(text);
        inputToolbar.add(emoticonBtn);
        inputToolbar.add(screenCaptureBtn);
        if (entity instanceof IMUser) {
            inputToolbar.add(shakeBtn);
        }
        inputToolbar.add(picturesBtn);
        inputToolbar.add(historyBtn);

        inputToolbar.addToEnd(sendBtn);
        inputToolbar.addSpacingToEnd(30);

        // 输入小工具栏
        inputToolbar.setFloatable(false);
        inputToolbar.setToolbarStyle(ToolbarStyle.attached);
        inputToolbar.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
    }

    private void initInputToolbarListener() {
        sendBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMsg(contentInput.getRichItems());
            }
        });
    }

    private void sendMsg(List<UIRichItem> richItems) {

    }

    private void setInputButtonStyle(WebButton webButton) {
        webButton.setRound(2);
        webButton.setRolloverDecoratedOnly(true);
    }

    public void showMsg(IMMsg msg) {
        isAppendMsg = true;
        msgGroupPanel.add(new MsgPane(msg));
        msgGroupPanel.revalidate();
    }

    public void update() {
        ImageIcon icon = UIUtils.Bean.byteToIcon(entity.getAvatar(), 40, 40);
        avatarImage.setIcon(icon);
        nickLabel.setText(entity.getNick());
        signLabel.setText(entity.getSign());
    }

    @Override
    public void installSkin(SkinService skinService) {
        super.installSkin(skinService);

        // input toolbar icons
        textBtn.setIcon(skinService.getIconByKey("chat/toolbar/text"));
        emoticonBtn.setIcon(skinService.getIconByKey("chat/toolbar/emoticon"));
        screenCaptureBtn.setIcon(skinService.getIconByKey("chat/toolbar/screenCapture"));
        shakeBtn.setIcon(skinService.getIconByKey("chat/toolbar/shake"));
        picturesBtn.setIcon(skinService.getIconByKey("chat/toolbar/pictures"));
        historyBtn.setIcon(skinService.getIconByKey("chat/toolbar/history"));
        sendBtn.setIcon(skinService.getIconByKey("chat/toolbar/send"));

        inputToolbar.setPainter(new ColorPainter(new Color(230, 230, 230)));
        inputPanel.setPainter(new ColorPainter(new Color(250, 250, 250)));
        msgGroupPanel.setPainter(new ColorPainter(new Color(250, 250, 250)));
        contentInput.setBackground(new Color(250, 250, 250));
        headerPanel.setPainter(skinService.getPainterByKey("chat/navBg"));

    }
}
