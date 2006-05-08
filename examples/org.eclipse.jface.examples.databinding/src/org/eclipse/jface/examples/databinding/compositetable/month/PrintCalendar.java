/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jface.examples.databinding.compositetable.month;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class PrintCalendar {
   /** The names of the months */
   String[] months = {
      "January", "February", "March", "April",
      "May", "June", "July", "August",
      "September", "October", "November", "December"
   };

   /** The days in each month. */
   public final static int[] dom = {
         31, 28, 31, 30,   /* jan feb mar apr */
         31, 30, 31, 31, /* may jun jul aug */
         30, 31, 30, 31   /* sep oct nov dec */
   };

   /** Compute which days to put where, in the Cal panel */
   public void print(int mm, int yy) {
      /** The number of days to leave blank at the start of this month */
      int leadGap = 0;

      System.out.print(months[mm]);      // print month and year
      System.out.print(" ");
      System.out.print(yy);
      System.out.println();

      if (mm < 0 || mm > 11)
         throw new IllegalArgumentException("Month " + mm + " bad, must be 0-11");
      GregorianCalendar calendar = new GregorianCalendar(yy, mm, 1);

      System.out.println("Su Mo Tu We Th Fr Sa");

      // Compute how much to leave before the first.
      // get(DAY_OF_WEEK) returns 0 for Sunday, which is just right.
      leadGap = calendar.get(Calendar.DAY_OF_WEEK)-1;

      int daysInMonth = dom[mm];
      if (calendar.isLeapYear(calendar.get(Calendar.YEAR)) && mm == 1)
         ++daysInMonth;

      // Blank out the labels before 1st day of month
      for (int i = 0; i < leadGap; i++) {
         System.out.print("   ");
      }

      // Fill in numbers for the day of month.
      for (int i = 1; i <= daysInMonth; i++) {

         // This "if" statement is simpler than fiddling with NumberFormat
         if (i<=9)
            System.out.print(' ');
         System.out.print(i);

         if ((leadGap + i) % 7 == 0)      // wrap if end of line.
            System.out.println();
         else
            System.out.print(' ');
      }
      System.out.println();
   }

   /** For testing, a main program */
   public static void main(String[] av) {
      int month, year;

      PrintCalendar cp = new PrintCalendar();

      // print the current month.
      if (av.length == 2) {
         cp.print(Integer.parseInt(av[0])-1, Integer.parseInt(av[1]));
      } else {
         Calendar c = Calendar.getInstance();
         cp.print(c.get(Calendar.MONTH), c.get(Calendar.YEAR));
      }
   }
}
