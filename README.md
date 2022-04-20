# Genetic Algorithm  

## Generating first- and second-order Nearly Orthogonal Latin Hypercube designs and Nearly Orthogonal/Balanced designs  

Copyright (c) 2012  Alex D. MacCalman  
Please contact Alex MacCalman at alex.maccalman@gmail.com for any errors encountered.

This readme describes how to use the workbook titled DesignCreatorv2.xlsm. This file along with the DOE.jar file are what you need to run the design creator algorithm. The source code for the genetic algorithm is contained in the DesignAlgorithm directory.  

This design creator is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License (provided on the "glpl" worksheet), or (at your option) any later version.

This design creator is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

**Purpose**
This workbook is a tool for making it easier to design large-scale simulation experiments with high-order complex response output behavior. The designs created with this worksheet nearly guarantee that all first and second order terms are not confounded with others and provide an excellent space-filling property that enables the detection of model bias and the presence of step functions.  The purpose of this tool is to create designs for a mix of continuous, discrete, binary, and categorical variables.

(the workbook macros must be enabled in order to use any of the macro buttons)

For a more in depth description of this Design Creator Tool, see the Appendix in MacCalman (2013) for the User Manual.

**How to create a design.**
Note: In order to run the algorithm you must have Java Platform (JDK) downloaded on your computer. If required, go to the following Oracle website link to download it:

<http://www.oracle.com/technetwork/java/javase/downloads/index.html>

**For Windows Computers:**

1. Enter the desired input parameters in the Front End worksheet.  
2. Press the "Run Algorithm (Windows Computer Only)" button.  
Note: you can also run the algorithm from the command window by pressing the "Create Flat Files" button, changing directories to the folder the DOE.jar file is saved and entering the following:  runit.bat  

**For MAC Computers:**

1. Enter the desired input parameters in the Front End worksheet.  
2. Press the "Create Flat Files" Button.  
3. Open the Terminal window and navigate to the folder this workbook and DOE.jar file is saved (you can use the "Open Terminal" Button to the right or the Go2Shell application in the Finder window).  
4. From the Terminal prompt, enter the following and press enter:  

```bash
. ./runit.txt  
```

After the algorithm is complete, the design will be saved as a .csv file to the same folder the DOE.jar file is saved.

**Translating the Design.** Perform the following steps to create a worksheet that allows you to translate the design factors to the desired ranges:

1. Paste the design from the .csv file into the Coded Design worksheet.
2. Press the Create Translation Worksheet button.
3. Within the Translated Design worksheet, fill in the **GREEN CELLS only** with the appropriate low level, high level, and factor name.  Rounding a factor column will change the absolute maximum pairwise correlation of the design.  We do not recommend rounding any of the factors to create discrete designs because the algorithm has the ability to create a custom design with the specified number of discrete and categorical factors with their unique number of levels. For discrete factors, you can scale the settings by a constant if you desire.
Your design will show up in the light yellow entries below the factor level names.  You can print the worksheet to a comma delimited file and then eliminate the first few lines, or else copy and paste special the table values to a new Excel worksheet.
The yellow (output) spaces are protected, for a reason. **DO NOT** unprotect these cells type and type in entries. If you do this and save the worksheet, then the design may become corrupted.  

**Balance Check**
This worksheet calculates the minimum analytically achievable imbalance for a given number of discrete or categorical factor levels.  Use this worksheet to help decide how many design points you need to ensure the design's imbalance is minimized.  The algorithm will attempt to find discrete or categorical factors with an imbalance < 0.1.  After 50 attempts, if the algorithm did not find a column, then it will return the column with the lowest imbalance.  There are some design point and level combinations that cannot achieve a 0.1 balance.  This worksheet will help guide which design points are feasible for a given number of factor levels.

**Cataloged Designs**
We have cataloged 2<sup>nd</sup> Order NOLH for up to 12 factors so that the user does not have to recreate them.  To augment any of the cataloged designs with other factors types, copy the design into the Start Design worksheet and set the Start Design parameter to TRUE in the Front End worksheet. Be sure that the number of design points in the Start Design worksheet matches the design points set in the Front End worksheet.  To add 2<sup>nd</sup> order discrete factors to an existing 2<sup>nd</sup> Order NOLH, we recommend deleting columns from the original cataloged design in the Start Design worksheet and let the algorithm append the discrete columns to the design.

**Categorical Design**
This worksheet allows the user to create the dummy variables for each categorical factor. Within the Dummy Variables worksheet, the user can check the first-order correlations among the columns in the Dummy Variables worksheet.  

**Design Tool Macros.** The purpose of the Design Tools is to allow the user to check a design's correlation distribution and space-filling property.
In the Design Tools worksheet there are macro buttons that perform the following operations:

1. Calculate the ML<sub>2</sub> space-filling metric.
2. Center a design by subtracting each column's mean.  
3. Create the design's higher order terms (up to fourth order). Be sure to center the design before pressing these macro buttons. Only press the macro buttons once.
4. Calculate the design's maximum pairwise correlation.
5. Calculate the distribution of pairwise correlations and insert them into the Abs Corr Distro worksheet.
Note: It is important to center a design before creating the higher order terms and calculating the maximum absolute pairwise correlation.  If not, then the correlation between a linear column and its quadratic will be close to 1.0.

ML<sub>2</sub> space-filling metric: The modified L2 discrepancy  (ML<sub>2</sub>) is a space-filling measure used to assess how well a design covers the entire design region; the smaller the value, the better a design’s space-filling property. We can interpret the magnitude of a design’s ML<sub>2</sub> by comparing it with another design’s ML<sub>2</sub>; the design with a smaller ML<sub>2</sub> fills the design space better. For details about the ML<sub>2</sub> metric see  

     Hickernell, F. J. 1998. A generalized discrepancy and quadrature error bound. Mathematics of computation 67, 221, 299–322.

**What to call the designs.**
The NOLH designs stand for "Nearly Orthogonal Latin Hypercubes."  Nearly orthogonal means that the maximum absolute pairwise correlation between any two columns in a regression matrix is minimal.  A 2<sup>nd</sup> Order NOLH is a design where the maximum absolute pairwise correlation between all second-order terms is minimal (linear, quadratic and two-way interactions).  Designs that include discrete or categorical factors are call NO/B designs, which stands for "Nearly Orthogonal/Balanced" designs suitable for mixed factor types (continuous, discrete, and categorical).

**Acknowledgments**
This Design Creator Tool was created by Alex MacCalman during his doctoral studies while at the Naval Postgraduate school.  The genetic algorithm that creates the designs was coded by Alex MacCalman under the close supervision of Dr. Hélcio Vieira Jr. For more details about the properties or application of NOLH or NOB designs, see

     MacCalman, A. D. 2013. Flexible Space-Filling Designs for Complex System Simulations.  Doctoral dissertation. Monterey, CA, Naval 
     Postgraduate School.

     MacCalman, A. D., H. Vieira Jr., and T. W. Lucas.  2012.  Second Order Nearly Orthogonal Latin Hypercubes for Exploring Stochastic Simulations.
     Working paper, MOVES Institute, Naval Postgraduate School, Monterey, CA.

     Hernandez, A.S. 2008. Breaking Barriers to Design Dimensions in Nearly Orthogonal Latin Hypercubes. Doctoral dissertation. Monterey, CA, Naval 
     Postgraduate School.

     Hernandez, A.S., T.W. Lucas, and M. Carlyle.  2012.  Constructing nearly orthogonal Latin hypercubes for any nonsaturated run-variable combination.
     Working paper, Department of Operations Research, Naval Postgraduate School, Monterey, CA.
    
     Vieira, Jr., H., Sanchez, S. M., Kienitz, K. H., Belderrain, M. C. N. 2011. Generating and improving orthogonal designs by using mixed integer   
     programming. European Journal of Operational Research 215, 629–638.

     Vieira, Jr., H., Sanchez, S. M., Kienitz, K. H., Belderrain, M. C. N. 2012. Conducting trade-off analyses via simulation:  Efficient nearly orthogonal nearly balanced 
     mixed designs.  Working paper, Operations Research Department, Naval Postgraduate School, Monterey, CA.

     Kleijnen, J. P. C., S. M. Sanchez, T. W. Lucas, and T. M. Cioppa. 2005. A user's guide to the brave new world of
     designing simulation experiments.  INFORMS Journal on Computing 17(3): 263-289.

     Cioppa, T. M., and T. W. Lucas. 2007. Efficient Nearly Orthogonal and Space-Filling Latin Hypercubes. Technometrics 49, 1, 45–55.

Citing the spreadsheet
If you use this spreadsheet in a thesis or research paper, you can add the following citation to your list of references:

     MacCalman, A. D.  2012.  DesignCreatorv2 spreadsheet.  Available online via http://harvest.nps.edu
     [accessed xx/xx/20xx]

For more information...
Links to handouts, papers, presentations, and examples that you may find useful will be maintained at <http://harvest.nps.edu>, along with future updates of this worksheet.  

developed by Alex D. MacCalman, May 2013.
