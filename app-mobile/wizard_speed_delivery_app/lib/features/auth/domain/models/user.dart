import '../../../../shared/enums/enums.dart';

class User {
  final int id;
  final String identifiant;
  final String password;
  final UserRole role;

  const User({
    required this.id,
    required this.identifiant,
    required this.password,
    required this.role,
  });

  factory User.fromJson(Map<String, dynamic> json) => User(
    id: json['id'],
    identifiant: json['identifiant'],
    password: json['password'],
    role: UserRole.values.byName(json['role']),
  );

  Map<String, dynamic> toJson() => {
    'id': id,
    'identifiant': identifiant,
    'password': password,
    'role': role.name,
  };
}