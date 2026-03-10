-- ═══════════════════════════════════════════════════════════════════
-- JobQuest — Sample Data
-- Populates the database with realistic sample applications
-- so you can see the app in action immediately after startup.
-- ═══════════════════════════════════════════════════════════════════

INSERT OR IGNORE INTO job_applications (id, company_name, role, date_applied, status, job_link, notes) VALUES
(1,  'Google',       'Software Engineer Intern',     '2026-01-15', 'Interviewing',       'https://careers.google.com', 'Completed OA, phone screen scheduled for next week'),
(2,  'Microsoft',    'SDE Intern',                   '2026-01-20', 'Online Assessment',   'https://careers.microsoft.com', 'Received OA link, 2 coding questions'),
(3,  'Amazon',       'SDE Intern',                   '2026-02-01', 'Applied',             'https://amazon.jobs', 'Applied through referral'),
(4,  'Meta',         'Software Engineer Intern',     '2026-02-05', 'Rejected',            'https://metacareers.com', 'Did not pass resume screen'),
(5,  'Apple',        'iOS Developer Intern',         '2026-02-10', 'Applied',             'https://jobs.apple.com', 'Applied via university portal'),
(6,  'Netflix',      'Backend Engineer Intern',      '2026-02-15', 'Interviewing',        'https://jobs.netflix.com', 'Technical interview round 2 coming up'),
(7,  'Stripe',       'Full Stack Engineer Intern',   '2026-02-20', 'Offer',               'https://stripe.com/jobs', 'Received offer! $45/hr, SF office'),
(8,  'Spotify',      'Data Engineer Intern',         '2026-02-25', 'Applied',             'https://lifeatspotify.com', 'Applied to NYC office'),
(9,  'Airbnb',       'Frontend Engineer Intern',     '2026-03-01', 'Online Assessment',   'https://careers.airbnb.com', 'Take-home project due in 5 days'),
(10, 'Uber',         'Software Engineer Intern',     '2026-03-03', 'Applied',             'https://uber.com/careers', 'Team: Payments infrastructure'),
(11, 'LinkedIn',     'Software Engineer Intern',     '2026-03-05', 'Applied',             'https://careers.linkedin.com', 'Applied through LinkedIn Easy Apply'),
(12, 'Tesla',        'Embedded Systems Intern',      '2026-01-25', 'Rejected',            'https://tesla.com/careers', 'Position filled before review'),
(13, 'Salesforce',   'Software Engineer Intern',     '2026-02-08', 'Interviewing',        'https://salesforce.com/careers', 'Behavioral interview scheduled'),
(14, 'Adobe',        'Frontend Developer Intern',    '2026-02-12', 'Online Assessment',   'https://adobe.com/careers', 'HackerRank assessment received'),
(15, 'Palantir',     'SWE Intern',                   '2026-03-07', 'Applied',             'https://palantir.com/careers', 'Karat interview expected');
