package dk.rhmaarhus.shoplister.shoplister.utility;

import dk.rhmaarhus.shoplister.shoplister.model.Food;

/**
 * Created by rjkey on 07-12-2017.
 */

public interface Observer {
    public void update(Food[] foodArray);
}
