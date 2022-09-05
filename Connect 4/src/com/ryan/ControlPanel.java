package com.ryan;

import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class ControlPanel extends JPanel implements ActionListener {

    CntF game;
    GamePanel gamePanel;

    intSliderButton depth_slider;
    progressBar percentage_bar;
    selectorButton human_button;
    selectorButton AI_button;
    toggleButton human_move_first;
    iterativeToggle x_colour_toggle;
    iterativeToggle o_colour_toggle;
    basicButton new_game;
    basicButton undo_move;

    BufferedImage human_char;
    BufferedImage AI_char;

    String text_string;

    {
        try {
            human_char = ImageIO.read(getClass().getResourceAsStream("HumanDrawing.png"));
            AI_char = ImageIO.read(getClass().getResourceAsStream("ComputerDrawing.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    Color human_colour;
    Color AIColour;

    ControlPanel(CntF game, ClickListener clickListener, GamePanel gamePanel, int width, int height) {
        this.game = game;
        this.gamePanel = gamePanel;
        this.setPreferredSize(new Dimension(width, height));
        this.addMouseListener(clickListener);
        this.addMouseMotionListener(clickListener);

        int upper_bound = 3;
        int lower_bound = -12;

        String[] labelList = new String[upper_bound - lower_bound + 1];  // - 12 to 3 inclusive is 16 (12 + 1 (for 0) + 3)
        for (int i = 0; i < upper_bound - lower_bound + 1; i++) {
            if (i <= 3)
                labelList[i] = "This is a good depth for beginners.";
            else if (i <= 7)
                labelList[i] = "This is a good depth for intermediate players.";
            else if (i <= 11)
                labelList[i] = "This is a good depth for advanced players.";
            else if (i == 12)
                labelList[i] = "0 is the suggested depth for advanced players.";
            else
                labelList[i] = "This is a very advanced depth. Calculations may take a while.";
        }
        this.depth_slider = new intSliderButton(this, new int[] {80, 445}, new int[] {width - 80, 445}, null, lower_bound, null,
                upper_bound, "AI Depth", null, labelList, null, 0, null,
                null, null, null, null);
        this.percentage_bar = new progressBar(this, new int[] {40, 550}, new int[] {width - 40, 570},
                null, null, null, 2, null, "",
                "", null, null, null, "Calculating", null, Color.RED);

        this.human_move_first = new toggleButton(this, true, true, new int[] {width/2 - 60/2, 320},
                new int[] {60, 30}, new int[] {30, 30}, 5, "Play First", new Font("BM Jua", Font.BOLD, 18),
                Color.RED, "You play first.", "AI plays first.", "Click the board to start.", null, null, null,
                null, null, null, null, null);


        if (human_move_first.get_state()) {
            AIColour = gamePanel.o_colour;
            human_colour = gamePanel.x_colour;
        } else {
            AIColour = gamePanel.x_colour;
            human_colour = gamePanel.o_colour;
        }

        this.human_button = new selectorButton(this, new int[] {30, 70}, new int[] {width/2 - 30 - 10, 200}, new int[] {10, 10},
                5, human_char, 45, 7, 0.7, null, null,
                2, null, new String[] {"Click for:", "Playing:"},
                null, null, "Human vs. Human", null, null, 0,
                human_colour, new int[] {79, 9}, 27, 5, new int[][] {{97, 22}, {96, 18}});
        this.AI_button = new selectorButton(this, new int[] {width/2 + 10, 70}, new int[] {width/2 - 10 - 30, 200},
                new int[] {10, 10}, 5, AI_char, 30, 15, 0.4, null,
                null, 2, null, new String[] {"Click for:", "Playing:"}, null,
                null, "Human vs. AI", null, null, 1, AIColour, new int[] {64, 32},
                50, 3, new int[][] {{85, 95}, {89, 95}});

        this.x_colour_toggle = new iterativeToggle(this, new int[] {105, 650}, new int[] {50, 50},
                4, 5, new Color[] {Color.RED, Color.BLUE, Color.ORANGE, Color.MAGENTA, Color.BLACK},
                Color.BLACK, new String[] {"Player 1 Colour", "Player 1 Colour", "Player 1 Colour", "Player 1 Colour", "Player 1 Colour"},
                null, "Click to Change Colour", null, null, 0);
        this.o_colour_toggle = new iterativeToggle(this, new int[] {105, 740}, new int[] {50, 50},
                4, 5, new Color[] {Color.RED, Color.BLUE, Color.ORANGE, Color.MAGENTA, Color.BLACK},
                Color.BLACK, new String[] {"Player 2 Colour", "Player 2 Colour", "Player 2 Colour", "Player 2 Colour", "Player 2 Colour"},
                null, null, null, null, 1);

        this.undo_move = new basicButton(this, new int[] {260, 620}, new int[] {width - 260 - 35, 90}, 5,
                3, null, null, null, null, null,
                "Undo Move", null, null);
        this.new_game = new basicButton(this, new int[] {260, 730}, new int[] {width - 260 - 35, 90}, 5,
                3, null, null, null, null, null,
                "New Game", null, null);
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        RenderingHints rh = new RenderingHints(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHints(rh);

        Rectangle2D.Double borderLine = new Rectangle2D.Double(0, 0, 5, 1000);
        g2d.setColor(new Color(99, 184, 240));
        g2d.fill(borderLine);

        // title text
        g2d.setFont(new Font("BM Jua", Font.BOLD, 35));
        g2d.drawString("Control  Panel", (int) (getWidth()/2 - 0.5 * 13 * 0.5 * 35), 25);

        if (text_string != null) {
            g2d.setFont(new Font("BM Jua", Font.BOLD, 15));
            g2d.drawString(text_string, (int) (getWidth()/2 - 0.5 * text_string.length() * 0.455 * 15), 50);
        }

        // seperating line
        RoundRectangle2D.Double sep_line = new RoundRectangle2D.Double(30, 370, this.getWidth() - 60, 6, 6, 6);
        g2d.fill(sep_line);

        // colour border
        RoundRectangle2D.Double border = new RoundRectangle2D.Double(20, 610, 220, 220, 20, 20);
        g2d.setColor(Color.RED);
        g2d.setStroke(new BasicStroke(3));
        g2d.draw(border);


        if (human_move_first.get_state()) {
            AIColour = o_colour_toggle.getCurrent_state();
            human_colour = x_colour_toggle.getCurrent_state();
        } else {
            AIColour = x_colour_toggle.getCurrent_state();
            human_colour = o_colour_toggle.getCurrent_state();
        }

        depth_slider.paint(g2d);
        percentage_bar.paint(g2d);
        human_button.paint(g2d, human_colour);
        AI_button.paint(g2d, AIColour);
        human_move_first.paint(g2d);
        x_colour_toggle.paint(g2d);
        o_colour_toggle.paint(g2d);
        undo_move.paint(g2d);
        new_game.paint(g2d);
    }

    @Override
    public void actionPerformed(ActionEvent e) { // animation
    }

    private Timer make_timer(int count) {
        return new Timer(count, this);
    }
}

class intSliderButton {

    ControlPanel controlPanel;

    private final int startX;
    private final int startY;
    private final int endX;
    private final int endY;

    private final String start_label;
    private final String end_label;
    private final String[] labelList;
    private final String title;
    private final Font title_font;

    private final int lower_bound;
    private final int upper_bound;
    private double current_position;

    private Color line_colour;
    private Color circle_colour;
    private final Color normal_circle_color;
    private final Color clicked_circle_colour;
    private final Font font;
    private final Font labelFont;
    private final Color text_colour;

    private final int circle_rad;
    private final double x_per_num;
    private final double y_per_num;

    intSliderButton(ControlPanel controlPanel, int @NotNull [] starting_coord, int @NotNull [] ending_coord, String label1, int lower_bound,
                    String label2, int upper_bound, String title, Font title_font, String[] labelList, Font labelFont, int starting_position,
                    /* optionals: */ Color line_colour, Color dot_colour, Color clicked_dot_colour, Font font, Color text_colour) {
        this.controlPanel = controlPanel;

        this.startX = starting_coord[0];
        this.startY = starting_coord[1];
        this.endX = ending_coord[0];
        this.endY = ending_coord[1];
        this.start_label = Objects.requireNonNullElseGet(label1, () -> String.valueOf(lower_bound));
        this.end_label = Objects.requireNonNullElseGet(label2, () -> String.valueOf(upper_bound));
        this.title = title;
        if (labelList == null) {
            this.labelList = new String[upper_bound - lower_bound + 1];
            for (int i = 0; i <= upper_bound - lower_bound + 1; i++) {
                this.labelList[i] = "";
            }
        } else {
            this.labelList = labelList;
        }

        this.lower_bound = lower_bound;
        this.upper_bound = upper_bound;
        this.current_position = starting_position;

        if (line_colour == null) {
            this.line_colour = Color.BLACK;
        } else {
            this.line_colour = line_colour;
        }

        this.normal_circle_color = Objects.requireNonNullElseGet(dot_colour, () -> Color.RED);
        this.clicked_circle_colour = Objects.requireNonNullElseGet(clicked_dot_colour, () -> new Color(150, 0, 0));
        this.circle_colour = normal_circle_color;

        this.font = Objects.requireNonNullElseGet(font, () -> new Font("BM Jua", Font.BOLD, 20));
        this.labelFont = Objects.requireNonNullElseGet(font, () -> new Font("BM Jua", Font.BOLD, 15));
        this.title_font = Objects.requireNonNullElseGet(title_font, () -> new Font("BM Jua", Font.BOLD, 30));
        // what in the actual fuck does this do.

        this.text_colour = Objects.requireNonNullElseGet(text_colour, () -> circle_colour);

        this.circle_rad = (int) (this.font.getSize()/1.7 + 5);

        x_per_num = ((double) (endX - startX))/((double) (upper_bound - lower_bound));
        y_per_num = ((double) (endY - startY))/((double) (upper_bound - lower_bound));
    }

    public void paint(Graphics2D g2d) {
        // start label, end label, button label

        // line
        Line2D.Double line = new Line2D.Double(startX, startY, endX, endY);
        g2d.setColor(line_colour);
        g2d.draw(line);  // draw or fill?

        // circle
        int[] circle_coords = get_circle_coords();

        Ellipse2D.Double circle = new Ellipse2D.Double(circle_coords[0], circle_coords[1], circle_rad*2, circle_rad*2);
        g2d.setColor(circle_colour);
        g2d.fill(circle);

        // circle text
        g2d.setFont(font);
        g2d.setColor(Color.WHITE);
        String str = String.valueOf((int) current_position);
        int plusX = 15;
        if (str.equals("1"))
            plusX = 17;
        g2d.drawString(str, circle_coords[0] + plusX - 5*str.length(), circle_coords[1] + 2 * circle_rad - 10);
        // labels
        g2d.setColor(text_colour);
        g2d.drawString(start_label, startX - 10*start_label.length() - 17, startY + 5);
        g2d.drawString(end_label, endX + 17, endY + 5);
        // labelList
        g2d.setFont(labelFont);
        str = labelList[(int) current_position - lower_bound];
        g2d.drawString(str, (int) ((startX + endX)/2 - 0.5*str.length()*0.33*font.getSize()), (int) ((startY + endY)/2 + 1.85*font.getSize()));
        // title
        g2d.setFont(title_font);
        g2d.drawString(title, (int) ((startX + endX)/2 - 0.5*title.length()*0.48*title_font.getSize()), (int) ((startY + endY)/2 - 0.85*title_font.getSize()));

    }

    public int get_value() {
        return (int) current_position;
    }

    public void set_position(int coordinate) {
        current_position = (coordinate - startX)/x_per_num + lower_bound;
        if (current_position < lower_bound) {
            current_position = lower_bound;
        } else if (current_position > upper_bound) {
            current_position = upper_bound;
        }
        set_circle_colour(clicked_circle_colour);
        controlPanel.repaint();
    }

    public void reset_colours() {
        circle_colour = normal_circle_color;
        controlPanel.repaint();
    }

    public void set_circle_colour(Color colour) {
        circle_colour = colour;
    }

    public void set_line_colour(Color colour) {
        line_colour = colour;
    }

    private int[] get_circle_coords() {
        int x = (int) (((current_position - lower_bound) * x_per_num) + startX);
        int y = (int) (((current_position - lower_bound) * y_per_num) + startY);

        return new int[] {x - circle_rad, y - circle_rad};
    }

    public boolean point_on(int x, int y) {
        return (x > startX - circle_rad && x < endX + circle_rad && y > startY - circle_rad && y < endY + circle_rad);
    }
}

class progressBar {
    ControlPanel controlPanel;

    double percentage;

    private final int start_x;
    private final int start_y;
    private final int end_x;
    private final int end_y;
    private final int length;
    private final int width;

    private final Color line_colour;
    private final Color normal_bar_colour;
    private final Color finished_bar_colour;
    private final Color border_colour;
    private final int border_width;
    // private final boolean gradient;

    private final String start_label;
    private final String end_label;
    private String bottom_label;
    private final Font label_font;
    private final Color label_colour;

    private final String title;
    private final Font title_font;
    private final Color title_colour;

    // for paint

    //line
    Rectangle2D.Double bgLine;
    //border
    BasicStroke stroke;
    Line2D.Double topLine;
    Line2D.Double rightLine;
    Line2D.Double leftLine;
    Line2D.Double bottomLine;
    // % text
    Font percentFont;

    progressBar(ControlPanel controlPanel, int[] start_coords, int[] end_coords, Color line_color,
                Color normal_bar_colour, Color border_colour, int border_width, Color finished_bar_colour, String start_label, String end_label,
                Font label_font, Color label_colour, String bottom_label, String title, Font title_font, Color title_colour) {
        this.controlPanel = controlPanel;

        this.start_x = start_coords[0];
        this.start_y = start_coords[1];
        this.end_x = end_coords[0];
        this.end_y = end_coords[1];
        this.length = end_x - start_x;
        this.width = end_y - start_y;

        this.line_colour = Objects.requireNonNullElse(line_color, new Color(99, 184, 240));
        this.normal_bar_colour = Objects.requireNonNullElse(normal_bar_colour, Color.RED);
        this.border_colour = Objects.requireNonNullElse(border_colour, Color.BLACK);
        if (border_width == 0)
            this.border_width = 2;
        else
            this.border_width = border_width;
        this.finished_bar_colour = Objects.requireNonNullElse(finished_bar_colour, new Color(99, 184, 80));

        this.start_label = Objects.requireNonNullElse(start_label, "");
        this.end_label = Objects.requireNonNullElse(end_label, "");
        this.bottom_label = Objects.requireNonNullElse(bottom_label, "");
        this.label_font = Objects.requireNonNullElseGet(label_font, () -> new Font("BM Jua", Font.BOLD, 15));
        this.label_colour = Objects.requireNonNullElse(label_colour, this.line_colour);

        this.title = title;
        this.title_font = Objects.requireNonNullElseGet(title_font, () -> new Font("BM Jua", Font.BOLD, 30));
        this.title_colour = Objects.requireNonNullElse(title_colour, this.label_colour);

        // for paint

        // line
        bgLine = new Rectangle2D.Double(start_x + border_width, start_y + border_width,
                length - 2*border_width, width - 2*border_width);
        // border
        stroke = new BasicStroke(this.border_width);
        topLine = new Line2D.Double(start_x, start_y, end_x, start_y);
        rightLine = new Line2D.Double(end_x, start_y, end_x, end_y);
        leftLine = new Line2D.Double(start_x, start_y, start_x, end_y);
        bottomLine = new Line2D.Double(start_x, end_y, end_x, end_y);
        // % text
        this.percentFont = new Font(this.label_font.getName(), Font.BOLD, 14);
    }

    public void paint(Graphics2D g2d) {
        // line
        g2d.setColor(line_colour);
        g2d.fill(bgLine);
        // border
        g2d.setStroke(stroke);
        g2d.setColor(border_colour);
        g2d.draw(topLine);
        g2d.draw(rightLine);
        g2d.draw(leftLine);
        g2d.draw(bottomLine);
        // title
        g2d.setFont(title_font);
        g2d.setColor(title_colour);
        g2d.drawString(title, (int) ((start_x + end_x)/2 - 0.5*title.length()*0.46*title_font.getSize()), start_y - 15);

        // labels
        g2d.setFont(label_font);
        g2d.setColor(label_colour);
        g2d.drawString(start_label, (int) (start_x - start_label.length() * label_font.getSize() * 0.5), end_y - 2);
        g2d.drawString(end_label, end_x + 5, end_y - 2);
        conditional_label();
        g2d.drawString(bottom_label, (int) ((start_x + end_x)/2 - 0.5*bottom_label.length()*0.43*label_font.getSize()), (int) (end_y + label_font.getSize()*1.2));

        // progress bar
        int len = (int) ((percentage/100) * (length - 2*border_width));
        Rectangle2D.Double bar = new Rectangle2D.Double(start_x + border_width, start_y + border_width,
                len, width - 2*border_width);
        if (percentage >= 100)
            g2d.setColor(finished_bar_colour);
        else
            g2d.setColor(normal_bar_colour);
        g2d.fill(bar);

        // text
        if (percentage > 15) {
            String str = (int) percentage + "%";
            g2d.setFont(percentFont);
            g2d.setColor(Color.WHITE);
            g2d.drawString(str, (int) ((start_x + border_width) + 0.5 * len - 0.5*str.length()*0.5*percentFont.getSize()), end_y - border_width - 4);
        }
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
        controlPanel.repaint();
    }

    private void conditional_label() {
        if (controlPanel.depth_slider.get_value() >= 0 && percentage < 25 && percentage != 0) {
            this.bottom_label = "Calculation generally speeds up as more is calculated.";
        } else {
            this.bottom_label = "";
        }
    }

}

class selectorButton {
    ControlPanel controlPanel;

    private final int start_x;
    private final int start_y;
    private final int width;
    private final int height;
    private final BufferedImage image;
    private final int image_x;
    private final int image_y;
    private final double image_scale;

    private boolean enabled;
    private final Color bg_colour_enabled;
    private final Color bg_colour_disabled;
    private final Color[] border_colours;

    private final String[] state_labels;
    private final Font label_font;
    private final Color label_colour;
    private String current_label;
    private final String title;
    private final Font title_font;
    private final Color title_colour;

    private int current_state;
    private final int num_of_states;

    private Color head_colour;
    private final int[][] eyePosns;

    // for paint

    // background
    RoundRectangle2D.Double background;
    // border
    RoundRectangle2D.Double border;
    BasicStroke stroke;
    // head
    Ellipse2D.Double head;
    int head_size;
    int eye_size;

    selectorButton(ControlPanel controlPanel, int[] start_coords, int[] dimensions, int[] curve_dimensions, int border_width,
                   BufferedImage image, int image_x, int image_y, double image_scale, Color bg_colour_enabled, Color bg_colour_disabled,
                   int num_of_states, Color[] border_colours, String[] state_labels, Font label_font, Color label_colour,
                   String title, Font title_font, Color title_colour, int start_state, Color custom_colour, int[] head_pos,
                   int head_size, int eye_size, int[][] custom_Posns) {
        this.controlPanel = controlPanel;
        this.start_x = start_coords[0];
        this.start_y = start_coords[1];
        this.width = dimensions[0];
        this.height = dimensions[1];
        int curve_w = curve_dimensions[0];
        int curve_h = curve_dimensions[1];

        this.image = image;
        this.image_x = image_x;
        this.image_y = image_y;
        this.image_scale = image_scale;

        this.bg_colour_enabled = Objects.requireNonNullElse(bg_colour_enabled, new Color(99, 184, 240));
        this.bg_colour_disabled = Objects.requireNonNullElse(bg_colour_disabled, new Color((int) (99/1.25), (int) (184/1.25), (int) (240/1.25)));
        this.border_colours = Objects.requireNonNullElseGet(border_colours, () -> new Color[] {
                Color.GRAY, Color.RED
        });
        this.state_labels = Objects.requireNonNullElse(state_labels, new String[] {"", ""});
        this.label_font = Objects.requireNonNullElse(label_font, new Font("BM Jua", Font.BOLD, 15));
        this.label_colour = Objects.requireNonNullElse(label_colour, new Color(175, 20, 20));
        this.title = Objects.requireNonNullElse(title, "");
        this.title_font = Objects.requireNonNullElse(title_font, new Font("BM Jua", Font.BOLD, 20));
        this.title_colour = Objects.requireNonNullElse(title_colour, new Color(175, 20, 20));

        this.current_state = start_state;
        this.num_of_states = num_of_states;
        this.enabled = true;

        this.head_colour = Objects.requireNonNullElse(custom_colour, Color.BLACK);
        this.head_size = head_size;
        this.eyePosns = Objects.requireNonNullElseGet(custom_Posns, () -> new int[num_of_states][2]);
        this.eye_size = eye_size;

        // for paint

        // background
        background = new RoundRectangle2D.Double(start_x, start_y, width, height, curve_w, curve_h);
        // border
        border = new RoundRectangle2D.Double(start_x, start_y, width, height, curve_w, curve_h);
        stroke = new BasicStroke(border_width);
        // head

        head = new Ellipse2D.Double(start_x + head_pos[0], start_y + head_pos[1], head_size, head_size);
    }

    public void paint(Graphics2D g2d, Color head_colour) {
        this.head_colour = Objects.requireNonNullElse(head_colour, this.head_colour);
        // background
        if (enabled)
            g2d.setColor(bg_colour_enabled);
        else
            g2d.setColor(bg_colour_disabled);
        g2d.fill(background);
        // border
        g2d.setStroke(stroke);
        g2d.setColor(border_colours[current_state]);
        g2d.draw(border);
        // image
        g2d.drawImage(image, start_x + image_x, start_y + image_y, (int) (image.getWidth() * image_scale),
                (int) (image.getHeight() * image_scale), null);
        // label
        int end_x = start_x + width;
        int end_y = start_y + height;

        g2d.setFont(label_font);
        g2d.setColor(label_colour);
        String str = state_labels[current_state];
        g2d.drawString(str, (int) ((start_x + end_x)/2 - 0.5*str.length()*0.4*label_font.getSize()), (int) (end_y - 17 - 1.2*title_font.getSize()));
        // title
        g2d.setFont(title_font);
        g2d.setColor(title_colour);
        g2d.drawString(title, (int) ((start_x + end_x)/2 - 0.5*title.length()*0.47*title_font.getSize()),end_y - 17);
        // CUSTOM:
        // circle (head)
        g2d.setColor(head_colour);
        g2d.fill(head);

        int eyeX = eyePosns[current_state][0];
        int eyeY = eyePosns[current_state][1];

        g2d.setColor(Color.BLACK);
        Ellipse2D.Double eye = new Ellipse2D.Double(start_x + eyeX, start_y + eyeY, eye_size, eye_size);
        g2d.fill(eye);
    }

    public void setHead_colour(Color colour) {
        this.head_colour = colour;
    }

    public void setCurrent_state(int state) {
        this.current_state = state;
    }

    public int getCurrent_state() {
        return current_state;
    }

    public void changeCurrent_state(boolean p) {
        if (enabled) {
            current_state = (current_state + 1) % num_of_states;
            if (p)
                controlPanel.repaint();
        }
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        controlPanel.repaint();
    }

    public boolean mouseOn(int mouse_x, int mouse_y) {
        return (mouse_x > start_x && mouse_x < start_x + width && mouse_y > start_y && mouse_y < start_y + height);
    }
}

class toggleButton {
    ControlPanel controlPanel;

    private boolean state;
    private boolean enabled;

    private final int start_x;
    private final int start_y;
    private final int width;
    private final int height;
    private final int border_width;

    private final String title;
    private final Font title_font;
    private final Color title_colour;
    private final Color d_title_colour;
    private final String true_label;
    private final String false_label;
    private final String conditional_label;
    private final Font label_font;
    private final Color label_colour;

    private final Color dot_colour;
    private final Color disabled_dot_colour;
    private final Color true_bg_colour;
    private final Color d_true_bg_colour;
    private final Color false_bg_colour;
    private final Color d_false_bg_colour;
    private final Color border_colour;
    private final Color disabled_border_colour;

    // for paint

    // background
    private final RoundRectangle2D.Double background;
    // border
    private final BasicStroke stroke;

    toggleButton(ControlPanel controlPanel, boolean start_state, boolean start_enabled, int[] start_coords, int[] dimensions,
                 int[] curve_dims, int border_width, String title, Font title_font, Color title_colour, String true_label,
                 String false_label, String conditional_label, Font label_font, Color label_colour, Color dot_colour,
                 Color disabled_dot_colour, Color true_bg_colour, Color false_bg_colour, Color border_colour, Color disabled_border_colour) {
        this.controlPanel = controlPanel;

        this.state = start_state;
        this.enabled = start_enabled;

        this.start_x = start_coords[0];
        this.start_y = start_coords[1];
        this.width = dimensions[0];
        this.height = dimensions[1];
        this.border_width = border_width;
        int curve_width = curve_dims[0];
        int curve_height = curve_dims[1];

        this.title = Objects.requireNonNullElse(title, "");
        this.title_font = Objects.requireNonNullElse(title_font, new Font("BM Jua", Font.BOLD, 20));
        this.title_colour = Objects.requireNonNullElse(title_colour, new Color(99, 184, 240));
        this.d_title_colour = new Color((int) (this.title_colour.getRed()/1.5), (int) (this.title_colour.getGreen()/1.5), (int) (this.title_colour.getBlue()/1.5));

        this.true_label = Objects.requireNonNullElse(true_label, "");
        this.false_label = Objects.requireNonNullElse(false_label, "");
        this.conditional_label = Objects.requireNonNullElse(conditional_label, "");
        this.label_font = Objects.requireNonNullElse(label_font, new Font("BM Jua", Font.BOLD, 15));
        this.label_colour = Objects.requireNonNullElse(label_colour, new Color(99, 184, 240));

        this.dot_colour = Objects.requireNonNullElse(dot_colour, Color.RED);
        this.disabled_dot_colour = Objects.requireNonNullElse(disabled_dot_colour, Color.GRAY);
        this.true_bg_colour = Objects.requireNonNullElse(true_bg_colour, new Color(110, 230, 160) /* new Color(99, 184, 240)*/);
        this.d_true_bg_colour = new Color(this.true_bg_colour.getRed()/2, this.true_bg_colour.getGreen()/2, this.true_bg_colour.getBlue()/2);
        this.false_bg_colour = Objects.requireNonNullElse(false_bg_colour, Color.BLUE);
        this.d_false_bg_colour = new Color(this.false_bg_colour.getRed()/2, this.false_bg_colour.getGreen()/2, this.false_bg_colour.getBlue()/2);
        this.border_colour = Objects.requireNonNullElse(border_colour, /* new Color(99, 184, 240) */ Color.BLACK);
        this.disabled_border_colour = Objects.requireNonNullElse(disabled_border_colour, Color.DARK_GRAY);

        // for paint
        this.background = new RoundRectangle2D.Double(start_x, start_y, width, height, curve_width, curve_height);
        this.stroke = new BasicStroke(this.border_width);
    }

    public void paint(Graphics2D g2d) {
        // background
        if (enabled) {
            if (state)
                g2d.setColor(true_bg_colour);
            else
                g2d.setColor(false_bg_colour);
        } else {
            if (state)
                g2d.setColor(d_true_bg_colour);
            else
                g2d.setColor(d_false_bg_colour);
        }
        g2d.fill(background);
        // circle
        int x;
        if (state) {
            x = start_x + width - border_width/2 - (height - border_width);
        } else {
            x = start_x + border_width/2; }
        if (enabled) {
            g2d.setColor(dot_colour);
        } else {
            g2d.setColor(disabled_dot_colour); }
        Ellipse2D.Double circle = new Ellipse2D.Double(x, start_y + (double) border_width /2, height - border_width, height - border_width);
        g2d.fill(circle);
        // border
        if (enabled) {
            g2d.setColor(border_colour);
        } else {
            g2d.setColor(disabled_border_colour); }
        g2d.setStroke(stroke);
        g2d.draw(background);
        // title
        int end_x = start_x + width;
        int end_y = start_y + height;

        g2d.setFont(title_font);
        if (enabled)
            g2d.setColor(title_colour);
        else
            g2d.setColor(d_title_colour);
        g2d.drawString(title, (int) ((start_x + end_x)/2 - 0.5*title.length()*0.46*title_font.getSize()), start_y - 15);
        // labels
        g2d.setFont(label_font);
        g2d.setColor(label_colour);
        if (state)
            g2d.drawString(true_label, end_x + 15, (int) ((start_y + end_y)/2 + 0.3*label_font.getSize()));
        else
            g2d.drawString(false_label, (int) (start_x - false_label.length()*0.4*label_font.getSize()) - 15, (int) ((start_y + end_y)/2 + 0.3*label_font.getSize() - 1));
        if (enabled && !state) {
            g2d.drawString(conditional_label, end_x + 15, (int) ((start_y + end_y)/2 + 0.3*label_font.getSize()));
        }

    }

    public boolean mouseOn(int x, int y) {
        return (x > start_x && x < start_x + width && y > start_y && y < start_y + height);
    }

    public void switch_pos() {
        if (enabled) {
            state = !state;
            controlPanel.repaint();
        }
    }

    public void set_pos(boolean b) {
        state = b;
    }

    public void setEnabled(boolean b) {
        enabled = b;
        controlPanel.repaint();
    }

    public boolean get_state() {
        return state;
    }
}

class iterativeToggle {
    ControlPanel controlPanel;

    private final int start_x;
    private final int start_y;
    private final int width;
    private final int height;

    private final Color border_colour;

    private final String[] state_labels;
    private final Font label_font;
    private final String title;
    private final Font title_font;
    private final Color title_colour;

    private int current_state_num;
    private final Color[] states;
    private final int num_of_states;

    // for paint

    // background
    Ellipse2D.Double circle;
    // border
    BasicStroke stroke;

    iterativeToggle(ControlPanel controlPanel, int[] start_coords, int[] dimensions, int border_width, int num_of_states,
                    Color[] states, Color border_colour, String[] state_labels, Font label_font, /* Color label_colour, */
                     String title, Font title_font, Color title_colour, int start_state) {
        this.controlPanel = controlPanel;

        this.start_x = start_coords[0];
        this.start_y = start_coords[1];
        this.width = dimensions[0];
        this.height = dimensions[1];

        this.current_state_num = start_state;
        this.states = states;
        this.num_of_states = num_of_states;

        this.border_colour = Objects.requireNonNullElse(border_colour, Color.BLACK);
        this.state_labels = Objects.requireNonNullElse(state_labels, new String[num_of_states]);
        this.label_font = Objects.requireNonNullElse(label_font, new Font("BM Jua", Font.BOLD, 15));
        this.title = Objects.requireNonNullElse(title, "");
        this.title_font = Objects.requireNonNullElse(title_font, new Font("BM Jua", Font.BOLD, 20));
        this.title_colour = Objects.requireNonNullElse(title_colour, Color.RED); // new Color(175, 20, 20));  // new Color(99, 184, 240)

        // for paint

        // background
        circle = new Ellipse2D.Double(start_x, start_y, width, height);
        // border
        stroke = new BasicStroke(border_width);
    }

    public void paint(Graphics2D g2d) {
        // background (main circle)
        g2d.setColor(getCurrent_state());
        g2d.fill(circle);
        // border
        g2d.setStroke(stroke);
        g2d.setColor(border_colour);
        g2d.draw(circle);
        // label
        int end_x = start_x + width;
        int end_y = start_y + height;

        g2d.setFont(label_font);
        g2d.setColor(getCurrent_state());
        String str = state_labels[current_state_num];
        g2d.drawString(str, (int) ((start_x + end_x)/2 - 0.5*str.length()*0.42*label_font.getSize()), (int) (end_y + 10 + 0.8*label_font.getSize()));
        // title
        g2d.setFont(title_font);
        g2d.setColor(title_colour);
        g2d.drawString(title, (int) ((start_x + end_x)/2 - 0.5*title.length()*0.43*title_font.getSize()),start_y - 15);
    }

    public void setCurrent_state_num(int state, boolean p) {
        this.current_state_num = state % num_of_states;
        if (p) {
            controlPanel.repaint();
            controlPanel.gamePanel.repaint();
        }

    }

    public Color getCurrent_state() {
        return states[current_state_num];
    }

    public void changeCurrent_state(boolean p) {
        current_state_num = (current_state_num + 1) % num_of_states;
        if (p) {
            controlPanel.repaint();
            controlPanel.gamePanel.repaint();
        }
    }

    public boolean mouseOn(int mouse_x, int mouse_y) {
        return (mouse_x > start_x && mouse_x < start_x + width && mouse_y > start_y && mouse_y < start_y + height);
    }
}

class basicButton {
    ControlPanel controlPanel;

    private int state;  // 0 = disabled, 1 = normal, 2 = clicked

    private final int start_x;
    private final int start_y;
    private final int width;
    private final int height;

    private final Color bg_colour;
    private final Color clicked_bg_colour;
    private final Color disabled_bg_colour;
    private final Color border_colour;
    private final Color disabled_border_colour;

    private final String text;
    private final Font text_font;
    private final Color text_colour;

    // for paint
    RoundRectangle2D.Double background;
    BasicStroke stroke;

    basicButton(ControlPanel controlPanel, int[] start_coords, int[] dimensions, int curve_len, int border_width,
                Color bg_colour, Color clicked_bg_colour, Color disabled_bg_colour, Color border_colour, Color disabled_border_colour,
                String text, Font text_font, Color text_colour) {
        this.controlPanel = controlPanel;

        this.start_x = start_coords[0];
        this.start_y = start_coords[1];
        this.width = dimensions[0];
        this.height = dimensions[1];

        this.bg_colour = Objects.requireNonNullElse(bg_colour, new Color(99, 184, 240));
        int red = (int) (this.bg_colour.getRed() * 1.3);
        if (red > 255) { red = 255;}
        int green = (int) (this.bg_colour.getGreen()*1.3);
        if (green > 255) { green = 255; }
        int blue = (int) (this.bg_colour.getBlue()*1.3);
        if (blue > 255) { blue = 255; }
        this.clicked_bg_colour = Objects.requireNonNullElse(clicked_bg_colour,
                new Color(red, green, blue));
        this.disabled_bg_colour = Objects.requireNonNullElse(disabled_bg_colour,
                new Color((int) (this.bg_colour.getRed() * 0.6), (int) (this.bg_colour.getGreen()*0.6), (int) (this.bg_colour.getBlue()*0.6)));
        this.border_colour = Objects.requireNonNullElse(border_colour, Color.BLACK);
        this.disabled_border_colour = Objects.requireNonNullElse(disabled_border_colour, Color.GRAY);

        this.text = text;
        this.text_font = Objects.requireNonNullElse(text_font, new Font("BM Jua", Font.BOLD, 25));
        this.text_colour = Objects.requireNonNullElse(text_colour, Color.RED);

        this.state = 1;

        // for paint
        this.background = new RoundRectangle2D.Double(start_x, start_y, width, height, curve_len, curve_len);
        this.stroke = new BasicStroke(border_width);
    }

    public void paint(Graphics2D g2d) {
        // background
        if (state == 0)
            g2d.setColor(disabled_bg_colour);
        else if (state == 1)
            g2d.setColor(bg_colour);
        else
            g2d.setColor(clicked_bg_colour);
        g2d.fill(background);
        // border
        if (state == 0)
            g2d.setColor(disabled_border_colour);
        else
            g2d.setColor(border_colour);
        g2d.setStroke(stroke);
        g2d.draw(background);
        // text
        g2d.setFont(text_font);
        g2d.setColor(text_colour);
        g2d.drawString(text, (int) ((2 * start_x + width)/2 - 0.5 * text.length() * 0.5 * text_font.getSize()),
                (int) ((2* start_y + height)/2 + 0.5 * 0.8 * text_font.getSize()));
    }

    public boolean mouseOn(int x, int y) {
        return (x > start_x && x < start_x + width && y > start_y && y < start_y + height && state != 0);
    }

    public void setState(int s, boolean p) {
        state = s;
        if (p)
            controlPanel.repaint();
    }

    public int getState() {
        return state;
    }
}