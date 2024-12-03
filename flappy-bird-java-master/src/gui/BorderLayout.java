// package gui;

// import java.awt.Component;
// import java.awt.Container;
// import java.awt.Dimension;
// import java.awt.Insets;
// import java.awt.LayoutManager;

// public class BorderLayout implements LayoutManager {
//     public static final String NORTH = "North";
//     public static final String SOUTH = "South";
//     public static final String EAST = "East";
//     public static final String WEST = "West";
//     public static final String CENTER = "Center";

//     private Component northComponent;
//     private Component southComponent;
//     private Component eastComponent;
//     private Component westComponent;
//     private Component centerComponent;

//     @Override
//     public void addLayoutComponent(String name, Component comp) {
//         if (NORTH.equals(name)) {
//             northComponent = comp;
//         } else if (SOUTH.equals(name)) {
//             southComponent = comp;
//         } else if (EAST.equals(name)) {
//             eastComponent = comp;
//         } else if (WEST.equals(name)) {
//             westComponent = comp;
//         } else if (CENTER.equals(name) || name == null) {
//             centerComponent = comp;
//         }
//     }

//     @Override
//     public void removeLayoutComponent(Component comp) {
//         if (comp == northComponent) {
//             northComponent = null;
//         } else if (comp == southComponent) {
//             southComponent = null;
//         } else if (comp == eastComponent) {
//             eastComponent = null;
//         } else if (comp == westComponent) {
//             westComponent = null;
//         } else if (comp == centerComponent) {
//             centerComponent = null;
//         }
//     }

//     @Override
//     public Dimension preferredLayoutSize(Container parent) {
//         // Implement the logic to calculate the preferred layout size
//         return new Dimension(400, 600); // Replace with your desired size
//     }

//     @Override
//     public Dimension minimumLayoutSize(Container parent) {
//         // Implement the logic to calculate the minimum layout size
//         return new Dimension(200, 300); // Replace with your desired size
//     }

//     @Override
//     public void layoutContainer(Container parent) {
//         Insets insets = parent.getInsets();
//         int width = parent.getWidth() - insets.left - insets.right;
//         int height = parent.getHeight() - insets.top - insets.bottom;

//         if (northComponent != null) {
//             Dimension northSize = northComponent.getPreferredSize();
//             northComponent.setBounds(insets.left, insets.top, width, northSize.height);
//         }

//         if (southComponent != null) {
//             Dimension southSize = southComponent.getPreferredSize();
//             southComponent.setBounds(insets.left, parent.getHeight() - insets.bottom - southSize.height, width, southSize.height);
//         }

//         if (eastComponent != null) {
//             Dimension eastSize = eastComponent.getPreferredSize();
//             eastComponent.setBounds(parent.getWidth() - insets.right - eastSize.width, insets.top, eastSize.width, height);
//         }

//         if (westComponent != null) {
//             Dimension westSize = westComponent.getPreferredSize();
//             westComponent.setBounds(insets.left, insets.top, westSize.width, height);
//         }

//         if (centerComponent != null) {
//             centerComponent.setBounds(insets.left, insets.top, width, height);
//         }
//     }
// }