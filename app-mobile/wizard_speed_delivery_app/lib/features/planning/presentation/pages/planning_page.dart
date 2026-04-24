import 'package:flutter/material.dart';
import '../../../../core/service/api_service.dart';
import '../../../../core/theme/app_theme.dart';
import '../../domain/models/tournee.dart';
import '../widgets/calendar_widget.dart';
import '../widgets/tournee_card.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import '../bloc/planning_bloc.dart';

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

  List<Tournee> _filterByDate(List<Tournee> tournees, DateTime date) {
    return tournees
        .where(
          (t) =>
              t.date.year == date.year &&
              t.date.month == date.month &&
              t.date.day == date.day,
        )
        .toList();
  }

  Set<DateTime> _markedDates(List<Tournee> tournees) {
    return tournees
        .map((t) => DateTime(t.date.year, t.date.month, t.date.day))
        .toSet();
  }

  @override
  Widget build(BuildContext context) {
    return BlocProvider(
        create: (_) {
          final driverId = ApiService().driverId ?? 0;
          return PlanningBloc()..add(PlanningLoadRequested(driverId));
        },
      child: Scaffold(
        appBar: AppBar(title: const Text('Planning')),
        body: BlocBuilder<PlanningBloc, PlanningState>(
          builder: (context, state) {
            if (state is PlanningLoading) {
              return const Center(
                child: CircularProgressIndicator(color: AppTheme.accent),
              );
            }
            if (state is PlanningError) {
              return Center(
                child: Text(
                  state.message,
                  style: const TextStyle(color: AppTheme.textMuted),
                ),
              );
            }
            if (state is PlanningLoaded) {
              final todayTournees = _filterByDate(
                state.tournees,
                _selectedDate,
              );
              return Column(
                children: [
                  CalendarWidget(
                    selectedDate: _selectedDate,
                    markedDates: _markedDates(state.tournees),
                    onDateSelected: (d) => setState(() => _selectedDate = d),
                  ),
                  Container(
                    margin: const EdgeInsets.symmetric(horizontal: 20),
                    height: 1,
                    color: AppTheme.divider,
                  ),
                  Expanded(
                    child: todayTournees.isEmpty
                        ? Center(
                            child: Column(
                              mainAxisAlignment: MainAxisAlignment.center,
                              children: [
                                Icon(
                                  Icons.event_available_rounded,
                                  size: 48,
                                  color: AppTheme.textMuted.withOpacity(0.4),
                                ),
                                const SizedBox(height: 12),
                                Text(
                                  'Aucune tournée ce jour',
                                  style: TextStyle(
                                    fontSize: 15,
                                    color: AppTheme.textMuted.withOpacity(0.6),
                                  ),
                                ),
                              ],
                            ),
                          )
                        : ListView.builder(
                            padding: const EdgeInsets.all(20),
                            itemCount: todayTournees.length,
                            itemBuilder: (_, i) =>
                                TourneeCard(tournee: todayTournees[i]),
                          ),
                  ),
                ],
              );
            }
            return const SizedBox.shrink();
          },
        ),
      ),
    );
  }
}
