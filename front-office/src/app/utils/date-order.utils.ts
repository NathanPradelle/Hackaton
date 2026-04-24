import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export function computeMinTomorrowDate(): string {
  const tomorrow = new Date();
  tomorrow.setHours(0, 0, 0, 0);
  tomorrow.setDate(tomorrow.getDate() + 1);
  return toDateInputString(tomorrow);
}

export function toDateInputString(date: Date): string {
  const year = date.getFullYear();
  const month = `${date.getMonth() + 1}`.padStart(2, '0');
  const day = `${date.getDate()}`.padStart(2, '0');
  return `${year}-${month}-${day}`;
}

export function toLocalDate(value: string): Date | null {
  const parts = value.split('-');
  if (parts.length !== 3) {
    return null;
  }

  const year = Number(parts[0]);
  const month = Number(parts[1]);
  const day = Number(parts[2]);
  if (!year || !month || !day) {
    return null;
  }

  return new Date(year, month - 1, day);
}

export function tomorrowOrLaterValidator(minTomorrowDate: string): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const value = control.value as string | null;
    if (!value) {
      return null;
    }

    const selectedDate = toLocalDate(value);
    const minDate = toLocalDate(minTomorrowDate);
    if (!selectedDate || !minDate) {
      return { invalidDate: true };
    }

    selectedDate.setHours(0, 0, 0, 0);
    minDate.setHours(0, 0, 0, 0);
    return selectedDate < minDate ? { beforeTomorrow: true } : null;
  };
}

