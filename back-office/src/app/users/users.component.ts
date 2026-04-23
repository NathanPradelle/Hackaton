import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { UserService } from './user.service';

export interface User {
  id_user: number;
  identifiant: string;
  role: string;
}

@Component({
  selector: 'app-users',
  imports: [RouterLink],
  templateUrl: './users.component.html'
})
export class UsersComponent {
  private userService = inject(UserService);
  users = this.userService.users;
}