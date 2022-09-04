package com.example.inframedic.util;
import java.util.HashMap;

public class Hospital {
    private static HashMap<String, String> hospitals = new HashMap<>();
    private Hospital instance;

    public Hospital getInstance() {
        if (instance == null) {
            instance = new Hospital();
        }
        return instance;
    }

    static String[] hospitalNames = {
            "All India Institute of Medical Sciences (AIIMS), Delhi",
            "Fortis Hospital, Kolkata",
            "Indraprastha Apollo Hospital, New Delhi",
            "Lilavati Hospital, Mumbai",
            "Christian Medical College, Vellore",
            "Tata Memorial Hospital, Mumbai",
            "Postgraduate Institute of Medical Education and Research, Chandigarh",
            "Jawaharlal Institute of Postgraduate Medical Education and Research, Puducherry",
            "Sankara Nethralaya, Chennai",
            "Kokilaben Hospital, Mumbai",
            "Manipal Hospital, Bangalore",
            "Apollo Hospitals, Bangalore",
            "Hiranandani Hospital, Mumbai",
            "Aditya Birla Memorial, Pune",
            "BLK Super Specialty Hospital, Delhi",
            "Saifee Hospital, Mumbai",
            "Columbia Asia Hospital, Kolkata",
            "Sri Ramachandra Medical Centre (SRMC), Chennai",
            "Narayana Health, Bengaluru",
            "King Edward Memorial Hospital",
            "Gleneagles Global Hospital, Chennai",
            "Nanavati Super Specialty Hospital, Mumbai",
            "Dharamshila Narayana Superspeciality Hospital, Delhi",
            "MIOT International, Chennai",
            "Indian Spinal Injuries Centre, Delhi",
            "NIMHANS (National Institute of Mental Health and Neurosciences)",
            "Fortis Escorts Heart Institute – Okhla Road, Delhi",
            "Medanta – The Medicity, Gurugram",
            "CARE Foundation Hospital, Hyderabad",
            "Ruby Hall Clinics, Pune",
            "Apollo Gleneagles Hospital, Kolkata",
            "Kamineni Hospitals, Hyderabad",
            "Sir Ganga Ram Hospital, Delhi",
            "The Bombay Hospital and Medical Research Centre, Mumbai",
            "KLE’S Dr Prabhakar Kore Hospital, Belgaum"
    };

    static String[] hospitalAddress = {
            "Sri Aurobindo Marg, Ansari Nagar, Ansari Nagar East, New Delhi.",
            "730, Anandapur, E.M. Bypass Road, Kolkata, West Bengal 700107, India",
            "Delhi Mathura Road, Sarita Vihar, New Delhi, India",
            "A-791, Bandra Reclamation, Bandra (W), Mumbai – 400050. India",
            "IDA Scudder Rd, Vellore, Tamil Nadu 632004, India",
            "Dr. E Borges Road, Parel East, Parel, Mumbai, Maharashtra 400012, India",
            "Madhya Marg, Sector 12, Chandigarh, 160012, India",
            "Jipmer Campus Rd, Gorimedu, Puducherry, 605006, India",
            "No. 41 (old 18), College Road, Chennai 600 006, Tamil Nadu, India.",
            "Rao Saheb, Achutrao Patwardhan Marg, Four Bungalows, Andheri West, Mumbai, Maharashtra 400053, India",
            "98, HAL Old Airport Rd, Kodihalli, Bengaluru, Karnataka 560017, India",
            "154 / 11, Bannerghatta Road Opp. I.I.M Bangalore – 560 076",
            "Hillside Rd, Hiranandani Gardens, Ramabai Ambedkar Nagar, Powai, Mumbai, Maharashtra 400076, India",
            "Aditya Birla Hospital Marg, Thergaon, Pimpri-Chinchwad, Maharashtra 411033, India",
            "Pusa Rd, Radha Soami Satsang, Rajendra Place, New Delhi, Delhi 110005, India",
            "15/17, Maharshi Karve Rd, Charni Road East, Opera House, Girgaon, Mumbai, Maharashtra 400004, India",
            "IB-193, IB Block, Sector-III, Bidhannagar, Kolkata, West Bengal 700091, India",
            "No.1, Ramachandra Nagar, Sri Ramachandra Nagar, Chennai, Tamil Nadu 600116, India",
            "Basant Health Centre Building No. 1, 18th Main Rd, Sector 3, HSR Layout, Bengaluru, Karnataka 560102, India",
            "Acharya Donde Marg, Parel East, Parel, Mumbai, Maharashtra 400012, India",
            "439, Embassy Residency Rd, Cheran Nagar, Perumbakkam, Chennai, Tamil Nadu 600100, India",
            "Swami Vivekananda Rd, near LIC, LIC Colony, Suresh Colony, Vile Parle West, Mumbai, Maharashtra 400056, India",
            "Metro Station, Dharamshila marg, Vasundhara Enclave Near Ashok Nagar, Dallupura, New Delhi, Delhi 110096, India",
            "4/112, Mount Poonamallee Rd, Sathya Nagar, Manapakkam, Chennai, Tamil Nadu 600089, India",
            "Vasant Kunj Marg, Opp Vasant Valley School, IAA Colony, Sector C, Vasant Kunj, New Delhi, Delhi 110070, India",
            "Hosur Rd, near Bangalore Milk Dairy, Hombegowda Nagar, Bengaluru, Karnataka 5600029, India",
            "Okhla road, Sukhdev Vihar Metro Station, New Delhi, Delhi 110025, India",
            "CH Baktawar Singh Rd, near Olympus, Medicity, Islampur Colony, Sector 38, Gurugram, Haryana 122001, India",
            "Avenue 4, Banjara Hills, Hyderabad, Telangana 500034, India",
            "40, Sasoon Rd, Sangamvadi, Pune, Maharashtra 411001, India",
            "58, Canal Circular Rd, Kadapara, Phool Bagan, Kankurgachi, Kolkata, West Bengal 700054, India",
            "Inner Ring Rd, Suryodaya Colony, Sarvodaya Colony, Central Bank Colony, Bahadurguda, Telangana 500068, India",
            "Sir Ganga Ram Hospital Marg, Old Rajinder Nagar, New Rajinder Nagar, New Delhi, Delhi 110060, India",
            "12, Vitthaldas Thackersey Marg, Near to Liberty Cinema, New Marine Lines, Mumbai, Maharashtra 400020, India",
            "NH Service Road, Basava Circle, Chikodi, Nehru Nagar, Belgaum, Karnataka 590010, India"
    };



    public static HashMap<String, String> getHospitals() {
        for(int i = 0; i < 35; i++) {
            hospitals.put(hospitalNames[i], hospitalAddress[i]);
        }
        return hospitals;
    }


}
