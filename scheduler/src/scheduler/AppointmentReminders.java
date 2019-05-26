/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.Alert;


/**
 *
 * @author Elliot Knuth
 */
public class AppointmentReminders implements Runnable{
    
    class AppointmentComparator implements Comparator<AppointmentDataModel> {

        @Override
        public int compare(AppointmentDataModel o1, AppointmentDataModel o2) {
            if (LocalTime.parse(o1.getStart().get().substring(o1.getStart().get().indexOf(" ") + 1, o1.getStart().get().length() - 2)).isBefore(
                LocalTime.parse(o2.getStart().get().substring(o2.getStart().get().indexOf(" ") + 1, o2.getStart().get().length() - 2)))) 
                return -1;
                else
                return 1;
        }
    
    }
    
    PriorityQueue<AppointmentDataModel> upcomingAppointmentsQueue = new PriorityQueue<>(new AppointmentComparator());
    PriorityQueue<AppointmentDataModel> alarmsQueue = new PriorityQueue<>(new AppointmentComparator());
    
    ScheduledExecutorService appointmentReminderService = Executors.newScheduledThreadPool(1);
    
    FilteredList<AppointmentDataModel> appointmentData = ((AppointmentsFXMLController)Start.controllers.get("Appointments"))
            .appointmentsData.filtered(data -> data.getDate().get().equals(
            String.valueOf(LocalDate.now().getYear())
                    .concat("-")
                        .concat(LocalDate.now().getMonthValue() < 10 ?
                                String.valueOf(0).concat(String.valueOf(LocalDate.now().getMonthValue())) : 
                                String.valueOf(LocalDate.now().getMonthValue()))
                            .concat("-")
                                .concat(String.valueOf(LocalDate.now().getDayOfMonth()))
            ));

    public AppointmentReminders() {
        Start.addExecutor(appointmentReminderService);
    }
    
    
    @Override
    public void run() {
        //Makes filtered list of appointments for today
           FilteredList<AppointmentDataModel> appointmentDataWinin30Minutes = appointmentData.filtered(data -> {
           LocalTime datasLocalTime = LocalTime.parse(data.getStart().get().substring(data.getStart().get().indexOf(" ") + 1)); 
           
           return (datasLocalTime.isAfter(LocalTime.now()) ||
                   datasLocalTime.minus(30, ChronoUnit.MINUTES) == LocalTime.now()) &&
                   !(datasLocalTime.isAfter(LocalTime.now().plus(30, ChronoUnit.MINUTES)));
        });
        
         System.out.println("Running appointmentReminders Thread at ".concat(LocalTime.now().toString()));
            if (!appointmentDataWinin30Minutes.isEmpty()) {
                
                appointmentLoop:
                for (AppointmentDataModel appointment : appointmentDataWinin30Minutes) {
                    if (!upcomingAppointmentsQueue.isEmpty()) {
                        for (AppointmentDataModel queueAppointment : upcomingAppointmentsQueue) {
                            
                            if (queueAppointment.getAppointmentId().get() == appointment.getAppointmentId().get() &&
                                queueAppointment.getStart().get().equals(appointment.getStart().get()))
                               continue appointmentLoop;
                        }
                    }
                    upcomingAppointmentsQueue.add(appointment);
                    alarmsQueue.add(appointment);
                    
                }
            }
            System.out.println("Alarm queue contains: ".concat(String.valueOf(alarmsQueue.size())).concat(" items"));
            System.out.println("Upcoming queue contains: ".concat(String.valueOf(upcomingAppointmentsQueue.size())).concat(" items"));
            LinkedList<AppointmentDataModel> removalList = new LinkedList<>(); 
           
            upcomingAppointmentsQueue.forEach(appointment -> {
                if (LocalTime.parse(appointment.getStart().get().substring(appointment.getStart().get().indexOf(" ") + 1)).isBefore(LocalTime.now()))
                    removalList.add(appointment);
            });
            
            removalList.forEach(appointment -> {
                if (upcomingAppointmentsQueue.contains(appointment))
                    upcomingAppointmentsQueue.remove(appointment);
            });
    }
    
    class createAlerts implements Runnable {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        
        @Override
        public void run() {
            System.out.println("Running createAlarm at ".concat(LocalTime.now().toString()));
           
            if (!alarmsQueue.isEmpty()) {
                AppointmentDataModel nextAppointment = alarmsQueue.poll();                    
                LocalTime startTime = LocalTime.parse(nextAppointment.getStart().get().substring(nextAppointment.getStart().get().indexOf(" ") + 1));
                long differenceInMinutes = Duration.between(LocalTime.now().truncatedTo(ChronoUnit.MINUTES), startTime.truncatedTo(ChronoUnit.MINUTES)).toMinutes();
                //long localTimePlus15 = Integer.parseInt(LocalTime.now().toString()) + differenceInMinutes;
                differenceInMinutes = differenceInMinutes >= 15 ? differenceInMinutes - 15 : 0;

                System.out.println("Notice to show ".concat(String.valueOf(differenceInMinutes)).concat(" from ").concat(LocalTime.now().toString()));
                appointmentReminderService.schedule(
                    () -> { Platform.runLater(() -> {

                    alert.setHeaderText(null);
                    alert.setTitle("Meeting");
                    alert.setContentText(nextAppointment.getStart().get().concat(String.valueOf(nextAppointment.getCustomerId().get())));

                    System.out.println("Set alert");
                    alert.setOnShowing(value -> System.out.println("Alert showing on ".concat(LocalTime.now().toString())));
                    alert.show();});
                    }, differenceInMinutes, TimeUnit.MINUTES);
            }
        }
    }
}
