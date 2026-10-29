CREATE TABLE specialties (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_specialties_name UNIQUE (name)
) ENGINE=InnoDB;

CREATE TABLE doctors (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    specialty_id BIGINT NOT NULL,
    qualifications VARCHAR(255) NOT NULL,
    experience_years INT NOT NULL,
    profile_image_url VARCHAR(500),
    slot_minutes INT NOT NULL DEFAULT 30,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_doctors_specialty FOREIGN KEY (specialty_id) REFERENCES specialties(id),
    CONSTRAINT chk_doctors_experience CHECK (experience_years >= 0),
    INDEX idx_doctors_specialty (specialty_id)
) ENGINE=InnoDB;

-- day_of_week: 1 = Monday ... 7 = Sunday
CREATE TABLE doctor_availability (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    doctor_id BIGINT NOT NULL,
    day_of_week TINYINT NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    CONSTRAINT fk_availability_doctor FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE,
    CONSTRAINT chk_availability_day CHECK (day_of_week BETWEEN 1 AND 7),
    CONSTRAINT chk_availability_time CHECK (end_time > start_time),
    CONSTRAINT uk_availability_doctor_day UNIQUE (doctor_id, day_of_week)
) ENGINE=InnoDB;

CREATE TABLE admins (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password_hash VARCHAR(100) NOT NULL,
    full_name VARCHAR(120) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_admins_username UNIQUE (username)
) ENGINE=InnoDB;

CREATE TABLE appointments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_reference VARCHAR(20) NOT NULL,
    patient_name VARCHAR(120) NOT NULL,
    patient_phone VARCHAR(15) NOT NULL,
    patient_email VARCHAR(150),
    doctor_id BIGINT NOT NULL,
    appointment_date DATE NOT NULL,
    appointment_time TIME NOT NULL,
    reason VARCHAR(300) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_appointments_reference UNIQUE (booking_reference),
    CONSTRAINT fk_appointments_doctor FOREIGN KEY (doctor_id) REFERENCES doctors(id),
    CONSTRAINT chk_appointments_status CHECK (status IN ('PENDING','CONFIRMED','COMPLETED','CANCELLED')),
    INDEX idx_appointments_slot (doctor_id, appointment_date, appointment_time),
    INDEX idx_appointments_status (status),
    INDEX idx_appointments_phone (patient_phone)
) ENGINE=InnoDB;

-- Sample data (fictional)
INSERT INTO specialties (name, description) VALUES
('Cardiology', 'Heart and cardiovascular care'),
('Neurology', 'Brain, spine and nerve disorders'),
('Pediatrics', 'Healthcare for infants and children'),
('Orthopedics', 'Bone, joint and sports injury care'),
('General Medicine', 'Primary care and health check-ups'),
('Dental Care', 'Preventive and restorative dentistry');

INSERT INTO doctors (name, specialty_id, qualifications, experience_years, slot_minutes) VALUES
('Dr. Madhavi Latha', 1, 'MBBS, MD, DM (Cardiology)', 14, 30),
('Dr. Sreenivasulu', 2, 'MBBS, MD, DM (Neurology)', 11, 30),
('Dr. Kamalnath', 3, 'MBBS, MD (Pediatrics)', 9, 30),
('Dr. Santhosh Kumar', 4, 'MBBS, MS (Orthopedics)', 12, 30),
('Dr. Padmavathi', 5, 'MBBS, MD (General Medicine)', 8, 30),
('Dr. Hemanth', 6, 'BDS, MDS (Prosthodontics)', 7, 30);

INSERT INTO doctor_availability (doctor_id, day_of_week, start_time, end_time) VALUES
(1,1,'10:00','14:00'),(1,2,'10:00','14:00'),(1,3,'10:00','14:00'),(1,4,'10:00','14:00'),(1,5,'10:00','14:00'),
(2,1,'11:00','15:00'),(2,2,'11:00','15:00'),(2,3,'11:00','15:00'),(2,4,'11:00','15:00'),(2,5,'11:00','15:00'),(2,6,'11:00','15:00'),
(3,1,'09:00','13:00'),(3,2,'09:00','13:00'),(3,3,'09:00','13:00'),(3,4,'09:00','13:00'),(3,5,'09:00','13:00'),(3,6,'09:00','13:00'),
(4,2,'16:00','20:00'),(4,3,'16:00','20:00'),(4,4,'16:00','20:00'),(4,5,'16:00','20:00'),(4,6,'16:00','20:00'),
(5,1,'09:00','17:00'),(5,2,'09:00','17:00'),(5,3,'09:00','17:00'),(5,4,'09:00','17:00'),(5,5,'09:00','17:00'),
(6,1,'10:00','18:00'),(6,3,'10:00','18:00'),(6,5,'10:00','18:00'),(6,6,'10:00','14:00');