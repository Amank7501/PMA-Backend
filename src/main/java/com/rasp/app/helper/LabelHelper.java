/*
 * Copyright 2010-2020 M16, Inc. All rights reserved.
 * This software and documentation contain valuable trade
 * secrets and proprietary property belonging to M16, Inc.
 * None of this software and documentation may be copied,
 * duplicated or disclosed without the express
 * written permission of M16, Inc.
 */

package com.rasp.app.helper;

import platform.webservice.bi.*;
import platform.resource.BaseResource;
import com.rasp.app.resource.*;
import platform.resource.BaseResource;
import platform.helper.BaseHelper;
import platform.db.*;
import java.util.*;

/*
 ********** This is a generated class Don't modify it.Extend this file for additional functionality **********
 * 
 */
 public class LabelHelper extends BaseHelper {
		public LabelHelper() {super(new Label());}
		private static LabelHelper instance;
		public static LabelHelper getInstance() {if (instance == null)	{instance = new LabelHelper();register_birecord();} return instance;}
		public static void register_birecord() {
			BIRecord record;
		}
}