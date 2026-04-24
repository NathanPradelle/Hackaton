import '../../../../shared/enums/enums.dart';

class User {
  final int id;
  final String identifiant;
  final UserRole role;

  const User({
    required this.id,
    required this.identifiant,
    required this.role,
  });

  factory User.fromJson(Map<String, dynamic> json) => User(
    id: json['id'],
    identifiant: json['identifiant'],
    role: UserRole.values.byName(json['role']),
  );

  Map<String, dynamic> toJson() => {
    'id': id,
    'identifiant': identifiant,
    'role': role.name,
  };
}
