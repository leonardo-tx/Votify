import { ReactNode, useEffect, useRef } from "react";
import styles from "./styles/Modal.module.css";

interface Props {
  children?: ReactNode;
  isOpen: boolean;
  onClose: () => void;
  id?: string;
}

export default function Modal({ children, isOpen, onClose, id }: Props) {
  const modalRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (isOpen) {
      document.body.style.overflow = "hidden";
    }

    return () => {
      document.body.style.overflow = "auto";
    };
  }, [isOpen]);

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (
        modalRef.current &&
        !modalRef.current.contains(event.target as Node)
      ) {
        onClose();
      }
    };

    if (isOpen) {
      document.addEventListener("mousedown", handleClickOutside);
    }

    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, [isOpen, onClose]);
  return (
    isOpen && (
      <div className={styles["modal-background"]}>
        <div ref={modalRef} className={styles["modal"]}>
          {children}
        </div>
      </div>
    )
  );
}
