/**
 * Represents a User, aligned with the backend User.java entity.
 */
export interface User {
  id: number;
  username: string;
  role: string; // Should match UserRole enum on backend (e.g., 'ADMIN', 'DRIVER', 'CLIENT')
  password?: string; // Optional, mainly for user creation/update
}