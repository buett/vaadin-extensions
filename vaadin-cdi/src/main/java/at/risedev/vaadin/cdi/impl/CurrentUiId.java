package at.risedev.vaadin.cdi.impl;

import java.io.Serializable;

public class CurrentUiId implements Serializable {

    private Integer uiId;

    public CurrentUiId(Integer uiId) {
        this.uiId = uiId;
    }

    public Integer getUiId() {
        return uiId;
    }
}
