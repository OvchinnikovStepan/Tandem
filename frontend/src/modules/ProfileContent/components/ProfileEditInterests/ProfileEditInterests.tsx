import { useState } from "react";
import { Plus } from "lucide-react";
import { SearchInput, Tag, PageTitle, Divider, Button } from "@/ui";

interface Interest {
  id: string;
  label: string;
}

interface ProfileEditInterestsProps {
  interests?: Interest[];
  onSave: (interests: Interest[]) => void;
  onBack: () => void;
}

const defaultInterests: Interest[] = [
  { id: "1", label: "Баскетбол" },
  { id: "2", label: "Фильмы" },
  { id: "3", label: "Футбол" },
  { id: "4", label: "Игры" },
  { id: "5", label: "Разработка" },
  { id: "6", label: "Музыка" },
];

export default function ProfileEditInterests({
  interests = defaultInterests,
  onSave,
  onBack,
}: ProfileEditInterestsProps) {
  const [searchQuery, setSearchQuery] = useState("");
  const [selectedInterests, setSelectedInterests] = useState<Interest[]>(interests);

  const handleRemoveInterest = (id: string) => {
    setSelectedInterests((prev) => prev.filter((i) => i.id !== id));
  };

  const handleSave = () => {
    onSave(selectedInterests);
  };

  return (
    <div
      className="bg-[#FEFEFE] overflow-hidden w-full"
      style={{
        boxShadow: "0px 2px 8px rgba(0, 0, 0, 0.25)",
        maxWidth: "975px",
        borderRadius: "24px",
        height: "559px",
        position: "relative",
      }}
    >

      <div
        className="absolute"
        style={{ left: "50%", transform: "translateX(-50%)", top: "26px" }}
      >
        <PageTitle>Интересы</PageTitle>
      </div>

      <div
        className="absolute flex items-center gap-4"
        style={{ left: "50%", transform: "translateX(-50%)", top: "119px" }}
      >

        <SearchInput
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
          placeholder="Введите название вашего интереса..."
        />

        <Button
          variant="primary"
          className="w-[138px] h-10 rounded-xl gap-1.5"
        >
          <span>Добавить</span>
          <Plus className="w-5 h-5 text-[#333333]" />
        </Button>
      </div>

      <div className="absolute" style={{ width: "935px", left: "20px", top: "198px" }}>
        <Divider />
      </div>

      <div
        className="absolute flex justify-center items-start"
        style={{ width: "100%", left: "0", top: "238px" }}
      >
        <div className="grid grid-cols-2 gap-x-10 gap-y-5 place-items-center">
          {selectedInterests.map((interest) => (
            <Tag
              key={interest.id}
              label={interest.label}
              onRemove={() => handleRemoveInterest(interest.id)}
              removeIcon="trash"
            />
          ))}
        </div>
      </div>

      <div className="absolute" style={{ width: "935px", left: "20px", top: "461px" }}>
        <Divider />
      </div>

      <div
        className="absolute"
        style={{ left: "50%", transform: "translateX(-50%)", top: "488px" }}
      >
        <Button
          variant="primary"
          onClick={handleSave}
          className="w-[100px] h-[45px] rounded-lg text-[15px]"
        >
          Сохранить
        </Button>
      </div>
    </div>
  );
}
