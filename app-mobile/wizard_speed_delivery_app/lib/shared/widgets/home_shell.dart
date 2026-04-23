import 'package:flutter/material.dart';
import '../../core/theme/app_theme.dart';
import '../../features/planning/presentation/pages/planning_page.dart';
import '../../features/navigation/presentation/pages/navigation_page.dart';
import '../../features/trips/presentation/pages/trips_page.dart';
import '../../features/profile/presentation/pages/profile_page.dart';

class HomeShell extends StatefulWidget {
  const HomeShell({super.key});

  @override
  State<HomeShell> createState() => _HomeShellState();
}

class _HomeShellState extends State<HomeShell> {
  int _i = 0;
  final _screens = const [
    PlanningPage(),
    NavigationPage(),
    TripsPage(),
    ProfilePage(),
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: IndexedStack(index: _i, children: _screens),
      bottomNavigationBar: Container(
        decoration: const BoxDecoration(
          border: Border(top: BorderSide(color: AppTheme.divider, width: 0.5)),
        ),
        child: BottomNavigationBar(
          currentIndex: _i,
          onTap: (i) => setState(() => _i = i),
          items: const [
            BottomNavigationBarItem(
              icon: Icon(Icons.calendar_month_rounded),
              label: 'Planning',
            ),
            BottomNavigationBarItem(
              icon: Icon(Icons.navigation_rounded),
              label: 'Navigation',
            ),
            BottomNavigationBarItem(
              icon: Icon(Icons.route_rounded),
              label: 'Trajets',
            ),
            BottomNavigationBarItem(
              icon: Icon(Icons.person_rounded),
              label: 'Profil',
            ),
          ],
        ),
      ),
    );
  }
}
