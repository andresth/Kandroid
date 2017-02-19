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

@SuppressWarnings("unused")
public class KanboardResult {
    public final String Command;
    public final JSONObject[] Result;
    public final KanboardRequest Request;
    public final int ReturnCode;

    public KanboardResult(KanboardRequest request, JSONObject[] json, int returnCode) {
        Request = request;
        Command = "";
        Result = json;
        ReturnCode = returnCode;
    }
}
