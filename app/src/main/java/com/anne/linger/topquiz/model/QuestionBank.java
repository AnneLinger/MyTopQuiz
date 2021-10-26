package com.anne.linger.topquiz.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * Created by Anne Linger on 01/10/2021.
 */
public class QuestionBank implements Serializable {

        private final List<Question> mQuestionList;
        private int mQuestionIndex;

        // Constructeur : à chaque objet QuestionBank créé, on assigne à ce dernier une liste de questions mélangées
        // 1 On valorise la propriété de l'objet mQuestionList en lui attribuant la valeur du paramètre du constructeur
        // 2 On mélange le contenu de cette propriété de l'objet
        public QuestionBank(List<Question> questionList) {
                mQuestionList = questionList;
                Collections.shuffle(mQuestionList);
        }

        //Méthode pour premier appel de Question
        public Question getCurrentQuestion() {
                return mQuestionList.get(mQuestionIndex);
        }

        //Méthode pour toutes questions suivantes
        public Question getNextQuestion() {
                mQuestionIndex++;
                return getCurrentQuestion();
        }
}
