import 'package:flutter/material.dart';
import '../../../../core/theme/app_theme.dart';

class InfoSection extends StatelessWidget {
  final String title;
  final IconData icon;
  final Map<String, String> rows;

  const InfoSection({super.key, required this.title, required this.icon, required this.rows});

  @override
  Widget build(BuildContext context) {
    return Container(
      width: double.infinity,
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(color: AppTheme.card, borderRadius: BorderRadius.circular(16)),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(children: [
            Icon(icon, color: AppTheme.accent, size: 20), const SizedBox(width: 10),
            Text(title, style: const TextStyle(fontSize: 16, fontWeight: FontWeight.w700, color: AppTheme.textPrimary)),
          ]),
          const SizedBox(height: 16),
          ...rows.entries.map((e) => Padding(
            padding: const EdgeInsets.only(bottom: 14),
            child: Row(mainAxisAlignment: MainAxisAlignment.spaceBetween, children: [
              Text(e.key, style: const TextStyle(fontSize: 14, color: AppTheme.textSecondary)),
              Text(e.value, style: const TextStyle(fontSize: 14, fontWeight: FontWeight.w600, color: AppTheme.textPrimary)),
            ]),
          )),
        ],
      ),
    );
  }
}