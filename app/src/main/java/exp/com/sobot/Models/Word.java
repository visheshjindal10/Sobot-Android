package exp.com.sobot.Models;


public class Word {

   String result;
    private tuc[] tuc;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Word.tuc[] getTuc() {
        return tuc;
    }

    public void setTuc(Word.tuc[] tuc) {
        this.tuc = tuc;
    }

    public class tuc{
        public Word.tuc.meanings[] getMeanings() {
            return meanings;
        }

        public void setMeanings(Word.tuc.meanings[] meanings) {
            this.meanings = meanings;
        }

        meanings[] meanings;


        public class meanings{
            String language;
            String text;

            public String getLanguage() {
                return language;
            }

            public void setLanguage(String language) {
                this.language = language;
            }

            public String getText() {
                return text;
            }

            public void setText(String text) {
                this.text = text;
            }
        }
    }
}
