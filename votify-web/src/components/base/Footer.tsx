import { SiGithub } from "react-icons/si";
import Button from "../shared/Button";

export default function Footer() {
  return (
    <footer className="flex justify-center items-center p-2">
      <Button
        as="a"
        id="git-repository-anchor"
        variant="text"
        href="https://github.com/leonardo-tx/Votify"
      >
        <SiGithub size={20} />
        Github
      </Button>
    </footer>
  );
}
