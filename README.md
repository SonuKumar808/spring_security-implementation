# spring_security-implementation
Implementation of Spring Security with the latest parameters and some updated beans.


✅ 1. AuthenticationManager Bean
@Bean
public AuthenticationManager getAuthManager(AuthenticationConfiguration configuration) throws Exception {
    return configuration.getAuthenticationManager();
}

🔍 What it does:
This bean retrieves the AuthenticationManager from Spring's AuthenticationConfiguration, which is auto-configured when Spring Boot detects Spring Security.
The AuthenticationManager is the central interface for authentication in Spring Security. When you call .authenticate(token) in your controller, you're calling this.
🧠 Behind the scenes: This AuthenticationManager is actually wired to use the AuthenticationProvider (e.g., DaoAuthenticationProvider) if you’ve registered one. If not, Spring uses its default behavior.

✅ 2. AuthenticationProvider Bean
@Bean
public AuthenticationProvider authProvider() {
    DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
    daoAuthenticationProvider.setUserDetailsService(userDetailService);
    daoAuthenticationProvider.setPasswordEncoder(getPasswordEncoder());
    return daoAuthenticationProvider;
}
🔍 What it does:
This bean creates a DaoAuthenticationProvider, which is one of Spring's built-in AuthenticationProvider implementations.
It uses your CustomUserDetailService to load user info (by username).
It uses your PasswordEncoder to check the provided password against the stored hashed password.

🔁 COMPLETE FLOW (Login Process)
@PostMapping("/login")
public ResponseEntity<ApiResponse<String>> loginAuthenticatedUser(@RequestBody LoginDto dto) {
    ApiResponse<String> response = new ApiResponse<>();

  // Create a token containing the username and password entered by the user.
  // This token will be passed to the AuthenticationManager to verify credentials.
  UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword());
    try {
        // Delegate authentication to the AuthenticationManager.
        // This will internally:
        // - Use the configured AuthenticationProvider (e.g., DaoAuthenticationProvider)
        // - That will call your CustomUserDetailService.loadUserByUsername()
        // - Then compare the password using the PasswordEncoder (BCrypt)
        Authentication authenticatedUser = authManager.authenticate(token);
        // If the user is successfully authenticated, return a 200 OK response
        if (authenticatedUser.isAuthenticated()) {
            response.setMessage("Login Successful");
            response.setStatus(200);
            response.setData("User has logged in successfully");
            return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getStatus()));
        }
    } catch (AuthenticationException e) {
        // If any exception occurs during authentication (e.g., bad credentials),
        // catch it and respond with an Unauthorized (401) status.
        e.printStackTrace(); // For debugging; remove in production.
    }
    // Return a failure response if authentication failed
    response.setMessage("Login Failed");
    response.setStatus(401);
    response.setData("Unauthorized Access: Invalid Username or Password");
    return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getStatus()));
}

🔁 Internal Flow Summary (Behind .authenticate()):
1. Controller receives login request.
2. Creates UsernamePasswordAuthenticationToken.
3. Passes token to AuthenticationManager.authenticate().
4. AuthenticationManager delegates to AuthenticationProvider (DaoAuthenticationProvider).
5. DaoAuthenticationProvider calls loadUserByUsername() from CustomUserDetailService.
6. It fetches user from DB and returns UserDetails object.
7. DaoAuthenticationProvider compares raw password (from token) and encoded password (from DB) using PasswordEncoder.
8. If match, returns an authenticated Authentication object.
9. Controller checks .isAuthenticated()` and sends 200 OK.

