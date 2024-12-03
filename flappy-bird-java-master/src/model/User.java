// // // package model;

// // // public class User {
// // //     private int id;
// // //     private String username;
// // //     private String password;
    
// // //     public User(int id, String username, String password) {
// // //         this.id = id;
// // //         this.username = username;
// // //         this.password = password;
// // //     }
    
// // //     public int getId() { return id; }
// // //     public String getUsername() { return username; }
// // //     public String getPassword() { return password; }
// // // }

// // package model;

// // public class User {
// //     private int id;
// //     private String username;
// //     private String password;
// //     private String email;  // Added email field
    
// //     // Constructor with all fields
// //     public User(int id, String username, String password) {
// //         this.id = id;
// //         this.username = username;
// //         this.password = password;
// //     }
    
// //     // Constructor with email
// //     public User(int id, String username, String password, String email) {
// //         this(id, username, password);
// //         this.email = email;
// //     }
    
// //     // Getters and setters
// //     public int getId() { return id; }
// //     public void setId(int id) { this.id = id; }
    
// //     public String getUsername() { return username; }
// //     public void setUsername(String username) { this.username = username; }
    
// //     public String getPassword() { return password; }
// //     public void setPassword(String password) { this.password = password; }
    
// //     public String getEmail() { return email; }
// //     public void setEmail(String email) { this.email = email; }
// // }

// package model;

// public class User {
//     private int id;
//     private String username;
//     private String password; // Consider making this transient or not storing plain text
//     private String email;

//     // Constructor
//     public User(int id, String username, String email) {
//         this.id = id;
//         this.username = username;
//         this.email = email;
//     }

//     // Alternate constructor if needed
//     public User(int id, String username, String password, String email) {
//         this.id = id;
//         this.username = username;
//         this.password = password;
//         this.email = email;
//     }

//     // Getters and setters
//     public int getId() {
//         return id;
//     }

//     public void setId(int id) {
//         this.id = id;
//     }

//     public String getUsername() {
//         return username;
//     }

//     public void setUsername(String username) {
//         this.username = username;
//     }

//     public String getEmail() {
//         return email;
//     }

//     public void setEmail(String email) {
//         this.email = email;
//     }

//     // Optional: Password getter/setter (be cautious with storing passwords)
//     public String getPassword() {
//         return password;
//     }

//     public void setPassword(String password) {
//         this.password = password;
//     }
// }

package model;

public class User {
    private int id;
    private String username;
    private String password;
    private String email;

    public User(int id, String username, String password, String email) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
    }

    // Getters
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getEmail() { return email; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setEmail(String email) { this.email = email; }
}
