package docmanagement.guiclient.frame.tool;

import java.awt.*;

public class GBC extends GridBagConstraints {
    static final int DEFAULT_WEIGHTX = 100;
    static final int DEFAULT_WEIGHTY = 100;

    public GBC(){
        this(0, 0);
    }

    public GBC(int gridx, int gridy){
        this(gridx,gridy, 1, 1);
    }

    public GBC(int gridx, int gridy, int gridwidth, int gridheight) {
        this.gridx = gridx;
        this.gridy = gridy;
        this.gridwidth = gridwidth;
        this.gridheight = gridheight;
        this.weightx = DEFAULT_WEIGHTX;
        this.weighty = DEFAULT_WEIGHTY;

        this.anchor = CENTER;
        this.fill = NONE;
        this.insets = new Insets(0, 0, 0, 0);
        this.ipadx = 0;
        this.ipady = 0;
    }

    public GBC setWeight(double weightx, double weighty){
        this.weightx = weightx;
        this.weighty = weighty;
        return this;
    }

    public GBC setAnchor(int anchor){
        this.anchor = anchor;
        return this;
    }

    public GBC setFill(int fill){
        this.fill = fill;
        return this;
    }

    public GBC setIpad(int ipadx, int ipady){
        this.ipadx = ipadx;
        this.ipady = ipady;
        return this;
    }

    public GBC setInsets(int top, int left, int bottom, int right){
        this.insets = new Insets(top, left, bottom, right);
        return this;
    }

    public GBC setInsets(int distance){
        this.insets = new Insets(distance, distance, distance, distance);
        return this;
    }

    public GBC setInsets(int distanceX, int distanceY){
        this.insets = new Insets(distanceY, distanceX, distanceY, distanceX);
        return this;
    }

}
