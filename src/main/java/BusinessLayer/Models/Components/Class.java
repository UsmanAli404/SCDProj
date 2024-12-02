package BusinessLayer.Models.Components;

import BusinessLayer.Models.Component;

import java.util.ArrayList;

public class Class extends Component {
    private ArrayList<String> attributes;
    private ArrayList<String> methods;

    public Class(String id) {
        super(id);
    }

    @Override
    public void draw() {

    }
}
