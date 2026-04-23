import 'package:flutter/material.dart';
import '../../../../core/theme/app_theme.dart';
import '../../../../shared/enums/enums.dart';
import '../../../../local/mock/mock_data.dart';
import '../../domain/models/tournee.dart';
import '../widgets/calendar_widget.dart';
import '../widgets/tournee_card.dart';

class PlanningPage extends StatefulWidget {
  const PlanningPage({super.key});

  @override
  State<PlanningPage> createState() => _PlanningPageState();
}

class _PlanningPageState extends State<PlanningPage> {
  late DateTime _selectedDate;

  @override
  void initState() {
    super.initState();
    _selectedDate = DateTime.now();
  }

  List<Tournee> _getTourneesForDate(DateTime date) {
    return MockData.tournees.where((t) =>
    t.date.year == date.year && t.date.month == date.month && t.date.day == date.day).toList();
  }

  Set<DateTime> get _datesWithTournees {
    return MockData.tournees.map((t) => DateTime(t.date.year, t.date.month, t.date.day)).toSet();
  }

  @override
  Widget build(BuildContext context) {
    final tourneesForDay = _getTourneesForDate(_selectedDate);

    return Scaffold(
      appBar: AppBar(title: const Text('Planning')),
      body: Column(children: [
        CalendarWidget(
          selectedDate: _selectedDate,
          markedDates: _datesWithTournees,
          onDateSelected: (d) => setState(() => _selectedDate = d),
        ),
        Container(margin: const EdgeInsets.symmetric(horizontal: 20), height: 1, color: AppTheme.divider),
        Expanded(
          child: tourneesForDay.isEmpty
              ? Center(child: Column(mainAxisAlignment: MainAxisAlignment.center, children: [
            Icon(Icons.event_available_rounded, size: 48, color: AppTheme.textMuted.withOpacity(0.4)),
            const SizedBox(height: 12),
            Text('Aucune tournée ce jour', style: TextStyle(fontSize: 15, color: AppTheme.textMuted.withOpacity(0.6))),
          ]))
              : ListView.builder(
            padding: const EdgeInsets.all(20),
            itemCount: tourneesForDay.length,
            itemBuilder: (_, i) => TourneeCard(tournee: tourneesForDay[i]),
          ),
        ),
      ]),
    );
  }
}