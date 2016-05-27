package recommender;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Miguel on 25-05-2016.
 */
public class RecommenderSystem
{
    private String RATINGS_PATH =  "Datasets/FilmTrust/ratings.txt";
    private String TRUST_PATH =  "Datasets/FilmTrust/trust.txt";

    private String[][] ratingsDataset;
    private String[][] trustDataset;
    private String[][] predictions;

    private Integer numberOfUsers, numberOfItems;

    public static void main(String[] args)
    {
        RecommenderSystem lol = new RecommenderSystem(1642, 2071);

        lol.importDataset();

        System.out.println("LOL");
        new java.util.Scanner(System.in).nextLine();
    }

    public RecommenderSystem(Integer numberOfUsers, Integer numberOfItems)
    {
        this.numberOfUsers = numberOfUsers;
        this.numberOfItems = numberOfItems;

        // Initialize lists:
        ratingsDataset = new String[numberOfUsers][numberOfItems];
        trustDataset = new String[numberOfUsers][numberOfUsers];
        predictions = new String[numberOfUsers][numberOfItems];

        importDataset();

        //System.out.println(requestItemsNotRated(1));

    }

    public List<Integer> requestItemsNotRated(Integer userID)
    {
        if (userID > numberOfUsers)
        {
            System.err.println("User ID not valid!");
            return new ArrayList<>();
        }

        List<Integer> returnArray = new ArrayList<>();

        // Return ratings not done already:
        for(int j = 0; j < ratingsDataset[userID - 1].length; j++)
        {
            if (ratingsDataset[userID - 1][j].equals("-1"))
                returnArray.add(j + 1);
        }

        return returnArray;
    }

    public void rateItem(Integer userID, Integer itemID, String rate)
    {
        if (userID > numberOfUsers)
        {
            System.err.println("User ID not valid!");
            return;
        }

        if (itemID > numberOfItems)
        {
            System.err.println("Item ID not valid!");
            return;
        }

        ratingsDataset[userID - 1][itemID - 1] = rate;

        // Update rate file:
        saveRatings();

        // Run algorithm:
        runAlgorithm();
    }

    public List<Integer> requestRecommendation(Integer numberOfItems)
    {
        // TODO
        return null;
    }

    public void changeTrust(Integer user1ID, Integer user2ID, String trust)
    {
        if (user1ID > numberOfUsers)
        {
            System.err.println("User1 ID not valid!");
            return;
        }

        if (user2ID > numberOfUsers)
        {
            System.err.println("User2 ID not valid!");
            return;
        }

        ratingsDataset[user1ID - 1][user2ID - 1] = trust;

        // Update trust file:
        saveTrust();

        // Run the algorithm:
        runAlgorithm();
    }

    private void runAlgorithm()
    {
        // TODO
    }

    private void importDataset()
    {
        // Delete all previous users:
        resetMatrixes();

        // Read all the new ones:
        readRatings();
        readTrust();
    }

    private void resetMatrixes()
    {
        for (String[] array : ratingsDataset)
            Arrays.fill(array, "-1");

        for (String[] array : trustDataset)
            Arrays.fill(array, "-1");

        for (String[] array : predictions)
            Arrays.fill(array, "-1");
    }

    private void readRatings()
    {
        try
        {
            CSVReader ratingsReader = new CSVReader(new FileReader(RATINGS_PATH), ' ');

            String[] nextLine;
            while ((nextLine = ratingsReader.readNext()) != null)
            {
                Integer userID = Integer.parseInt(nextLine[0]);
                Integer itemID = Integer.parseInt(nextLine[1]);
                String rating = nextLine[2];
                ratingsDataset[userID - 1][itemID - 1] = rating;
            }

            ratingsReader.close();
        } catch (IOException e)
        {
            System.err.println("Error loading ratings dataset file!");
            System.exit(1);
        }
    }

    private void saveRatings()
    {
        try
        {
            CSVWriter writer = new CSVWriter(new FileWriter(RATINGS_PATH), ' ', CSVWriter.NO_QUOTE_CHARACTER);

            for(int i = 0; i < ratingsDataset.length; i++)
                for(int j = 0; j < ratingsDataset[i].length; j++)
                {
                    if (!ratingsDataset[i][j].equals("-1"))
                        writer.writeNext(new String[]{String.valueOf(i + 1), String.valueOf(j + 1), ratingsDataset[i][j]});
                }

            writer.close();
        } catch (IOException e)
        {
            System.err.println("Error writing in ratings dataset file!");
            System.exit(1);
        }
    }

    private void readTrust()
    {
        try
        {
            CSVReader trustReader = new CSVReader(new FileReader(TRUST_PATH), ' ');

            String[] nextLine;
            while ((nextLine = trustReader.readNext()) != null)
            {
                Integer userID = Integer.parseInt(nextLine[0]);
                Integer user2ID = Integer.parseInt(nextLine[1]);
                String trust = nextLine[2];

                trustDataset[userID - 1][user2ID - 1] = trust;
            }

            trustReader.close();
        } catch (IOException e)
        {
            System.err.println("Error loading trust dataset file!");
            System.exit(2);
        }
    }

    private void saveTrust()
    {
        try
        {
            CSVWriter writer = new CSVWriter(new FileWriter(TRUST_PATH), ' ', CSVWriter.NO_QUOTE_CHARACTER);

            for(int i = 0; i < trustDataset.length; i++)
                for(int j = 0; j < trustDataset[i].length; j++)
                {
                    if (!trustDataset[i][j].equals("-1"))
                        writer.writeNext(new String[] {String.valueOf(i + 1), String.valueOf(j + 1), trustDataset[i][j]});
                }

            writer.close();
        } catch (IOException e)
        {
            System.err.println("Error writing in ratings dataset file!");
            System.exit(1);
        }
    }
}
