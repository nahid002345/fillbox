/*
 * Copyright (C) 2013 Andre Gregori and Mark Garro 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */

package com.nahid.diordna.fillbox;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * List of <code>Situation</code>s.  The purpose of this class is to make
 * public the protected <code>ArrayList</code> method
 * <code>removeRange()</code>.
 *
 */
public class SituationList extends ArrayList<Situation> {
	public void removeRange(int fromIndex, int toIndex){
		super.removeRange(fromIndex, toIndex);
	}
}