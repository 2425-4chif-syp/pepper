INSERT INTO pe_game_type (gt_id, gt_name) VALUES
                                              ('MEMORY', 'Memory'),
                                              ('TIC_TAC_TOE', 'TicTacToe'),
                                              ('TAG_ALONG_STORY', 'Mitmachgeschichten'),
                                              ('CATCH_THE_THIEF', 'Fang den Dieb');

INSERT INTO pe_game (g_is_enabled, g_gt_id, g_name, g_story_icon) VALUES
                                                                      (true, 'MEMORY', 'Memory für Anna', NULL),
                                                                      (true, 'TAG_ALONG_STORY', 'Geschichten aus Vorarlberg', NULL),
                                                                      (false, 'TAG_ALONG_STORY', 'Geschichten vom Hallstätter See', NULL),
                                                                      (true, 'TAG_ALONG_STORY', 'Geschichten aus Wien', NULL),
                                                                      (true, 'TAG_ALONG_STORY', 'Geschichten aus den Alpen', NULL),
                                                                      (true, 'TAG_ALONG_STORY', 'Geschichten aus Salzburg', NULL),
                                                                      (false, 'TAG_ALONG_STORY', 'Geschichten aus Graz', NULL),
                                                                      (true, 'TAG_ALONG_STORY', 'Geschichten aus Tirol', NULL),
                                                                      (true, 'TAG_ALONG_STORY', 'Geschichten vom Bodensee', NULL),
                                                                      (false, 'TAG_ALONG_STORY', 'Geschichten aus Linz', NULL),
                                                                      (true, 'TAG_ALONG_STORY', 'Geschichten aus der Wachau', NULL);

INSERT INTO pe_move (m_name, m_description) VALUES
                                                ('emote_hurra', 'Hurra'),
                                                ('essen', 'Essen'),
                                                ('gehen', 'Gehen'),
                                                ('hand_heben', 'Hand heben'),
                                                ('highfive_links', 'Highfive links'),
                                                ('highfive_rechts', 'Highfive rechts'),
                                                ('klatschen', 'Klatschen'),
                                                ('strecken', 'Strecken'),
                                                ('umher_sehen', 'Umhersehen'),
                                                ('winken', 'Winken');


INSERT INTO pe_step (st_index, st_g_id, st_i_id, st_m_id, st_text, st_duration_in_sec) VALUES
                                                                                           (1, 3, NULL, 1, 'GAME ID 2', 10),
                                                                                           (1, 3, NULL, 2, 'GAME ID 3', 15),
                                                                                           (2, 3, NULL, 3, 'GAME ID 2', 5),
                                                                                           (2, 3, NULL, 4, 'GAME ID 3', 10);



INSERT INTO pe_person (p_first_name, p_last_name, p_dob, p_room_no, p_isWorker, p_password, p_gender)
VALUES
    ('Anna', 'Müller', '1985-03-15', '101', TRUE, '$2a$10$examplehashedpassword1', TRUE),
    ('Franz', 'Huber', '1990-07-22', '102', FALSE, NULL, FALSE),
    ('Maria', 'Bauer', '1978-11-30', '103', TRUE, '$2a$10$examplehashedpassword3', TRUE),
    ('Michael', 'Wagner', '1982-05-10', '104', FALSE, NULL, FALSE),
    ('Sophie', 'Pichler', '1995-09-18', '105', TRUE, '$2a$10$examplehashedpassword5', TRUE),
    ('Thomas', 'Steiner', '1988-12-25', '106', FALSE, NULL, FALSE),
    ('Laura', 'Fischer', '1992-04-05', '107', TRUE, '$2a$10$examplehashedpassword7', TRUE),
    ('David', 'Weber', '1980-08-14', '108', FALSE, NULL, FALSE),
    ('Julia', 'Schmid', '1998-02-20', '109', TRUE, '$2a$10$examplehashedpassword9', TRUE),
    ('Markus', 'Wolf', '1975-06-12', '110', FALSE, NULL, FALSE);

-- INSERT INTO pe_game_score (gs_score, gs_date_time, gs_g_id, gs_p_id, gs_elapsed_time, gs_comment)
-- VALUES
--     (150, '2024-03-01 14:30:00', 1, 1, 120, '2x3'),
--     (200, '2024-03-01 15:00:00', 2, 2, 150, '2x4'),
--     (180, '2024-03-02 16:15:00', 3, 3, 130, '3x4'),
--     (220, '2024-03-03 17:45:00', 1, 4, 140, '4x4'),
--     (170, '2024-03-04 18:30:00', 2, 5, 110, '2x3'),
--     (190, '2024-03-05 19:00:00', 3, 6, 125, '2x4'),
--     (210, '2024-03-06 20:10:00', 1, 7, 135, '3x4'),
--     (230, '2024-03-07 21:30:00', 2, 8, 145, '4x4'),
--     (240, '2024-03-08 22:45:00', 3, 9, 155, '2x3'),
--     (260, '2024-03-09 23:50:00', 1, 10, 165, '2x4'),
--     (140, '2024-03-10 10:30:00', 2, 1, 115, '3x4'),
--     (155, '2024-03-11 11:20:00', 3, 2, 125, '4x4'),
--     (165, '2024-03-12 12:40:00', 1, 3, 135, '2x3'),
--     (175, '2024-03-13 13:50:00', 2, 4, 145, '2x4'),
--     (185, '2024-03-14 14:15:00', 3, 5, 155, '3x4'),
--     (195, '2024-03-15 15:25:00', 1, 6, 165, '4x4'),
--     (205, '2024-03-16 16:35:00', 2, 7, 175, '2x3'),
--     (215, '2024-03-17 17:45:00', 3, 8, 185, '2x4'),
--     (225, '2024-03-18 18:55:00', 1, 9, 195, '3x4'),
--     (235, '2024-03-19 19:05:00', 2, 10, 205, '4x4');



-- Binary image seed data was removed when image storage moved from PostgreSQL
-- LOBs to MinIO. Use POST /api/image to create image metadata and objects.
