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

  factory User.fromJson(Map<String, dynamic> json) {
    final roleStr = json['role'] as String? ?? '';
    final role = switch (roleStr) {
      'DRIVER' => UserRole.chauffeur,
      'CLIENT' => UserRole.client,
      'ADMIN' => UserRole.admin,
      _ => UserRole.client,
    };

    return User(
      id: json['id'] as int? ?? 0,
      identifiant: json['identifier'] as String? ?? '',
      role: role,
    );
  }
}