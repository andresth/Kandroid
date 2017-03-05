/*
 * Copyright 2017 Thomas Andres
 *
 * This file is part of Kandroid.
 *
 * Kandroid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Kandroid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package in.andres.kandroid.kanboard;

import org.json.JSONObject;

import java.io.Serializable;

@SuppressWarnings("unused")
public class KanboardColor implements Serializable {
    private String Id;
    private String Name;
    private Integer Background;
    private Integer Border;

    public KanboardColor(String id, JSONObject color) {
        Id = id;
        Name = color.optString("name");
        Background = KanboardAPI.parseColorString(color.optString("background"));
        Border = KanboardAPI.parseColorString(color.optString("border"));
    }

    public String getId() {
        return Id;
    }

    public String getName() {
        return Name;
    }

    public Integer getBackground() {
        return Background;
    }

    public Integer getBorder() {
        return Border;
    }
}
