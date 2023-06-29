import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Main {
    // 將ENV_CONFIGMAP(格式:XX_XXX_XX) 轉為HelmChat
    public static void main(String[] args) {
        BufferedReader reader;

        try {
            reader = new BufferedReader(new FileReader("D:\\tool\\test.yml"));
            String line = reader.readLine();
            LayerDTO all = new LayerDTO("all");
            LayerDTO root = new LayerDTO("vars");
            all.getChildDTO().add(root);
            List<String> value = new ArrayList<>();
            List<String> configMap = new ArrayList<>();
            while (line != null) {
                if(!line.trim().isEmpty()) {
                    line = line.substring(0, line.indexOf(":")).trim();
                    configMap.add(line+": "+String.format("\"{{ %s }}\"", line.toLowerCase().replace("_",".")));
                    line = line.toLowerCase();

                    String[] sp = line.split("_");
                    LayerDTO currentDTO = root;
                    for (int i = 0; i < sp.length; i++) {
                        String currentString = sp[i];
                        Optional<LayerDTO> optionalLayerDTO = currentDTO.getChildDTO().stream().filter(layerDTO -> layerDTO.getKey().equals(currentString))
                                .findFirst();
                        if(optionalLayerDTO.isEmpty())
                        {
                            LayerDTO childDTO = new LayerDTO(currentString);
                            currentDTO.getChildDTO().add(childDTO);
                            currentDTO = childDTO;
                        }
                        else {
                            currentDTO = optionalLayerDTO.get();
                        }
                    }
                }
                // read next line
                line = reader.readLine();
            }

            print(0, all, value);
            System.out.println("======== Value ========");
            value.forEach(System.out::println);
            System.out.println("======== ConfigMap ========");
            configMap.forEach(System.out::println);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void print(int layerCount, LayerDTO dto, List<String> value){
        value.add("  ".repeat(layerCount)+dto.getKey()+":");
        if(dto.getChildDTO()!=null && !dto.getChildDTO().isEmpty())
        {
            for (LayerDTO child: dto.getChildDTO())
            {
                print(layerCount+1, child, value);
            }
        }
    }
}

class LayerDTO {
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    private String key;

    public List<LayerDTO> getChildDTO() {
        return childDTO;
    }

    public void setChildDTO(List<LayerDTO> childDTO) {
        this.childDTO = childDTO;
    }

    private List<LayerDTO> childDTO;

    public LayerDTO(String key) {
        this.key = key;
        this.childDTO = new ArrayList<>();
    }
}