import {
  forwardRef,
  InputHTMLAttributes,
  ReactNode,
  TextareaHTMLAttributes,
} from "react";
import styles from "./styles/Input.module.css";

type InputProps = InputHTMLAttributes<HTMLInputElement> & {
  as?: "input";
};

type TextareaProps = TextareaHTMLAttributes<HTMLTextAreaElement> & {
  as: "textarea";
};

type Props = {
  variant?: "filled" | "outline" | "line";
  placeholder?: string;
  className?: string;
  startElement?: ReactNode;
  endElement?: ReactNode;
} & (InputProps | TextareaProps);

const Input = forwardRef<HTMLInputElement | HTMLTextAreaElement, Props>(
  (
    {
      as = "input",
      variant = "filled",
      placeholder,
      className,
      startElement,
      endElement,
      ...props
    },
    ref,
  ) => {
    const defaultClassNames = styles["common"] + " " + styles[variant];
    const startElementClassName =
      startElement === undefined || startElement === null
        ? ""
        : styles["input-div-start-element"] + " ";
    const endElementClassName =
      endElement === undefined || endElement === null
        ? ""
        : styles["input-div-end-element"] + " ";

    let inputNode: ReactNode;
    switch (as) {
      case "input":
        props = props as InputProps;
        inputNode = (
          <input
            ref={ref}
            placeholder={placeholder}
            className={defaultClassNames}
            {...props}
          />
        );
        break;
      case "textarea":
        props = props as TextareaProps;
        inputNode = (
          <textarea
            ref={ref}
            placeholder={placeholder}
            className={defaultClassNames}
            {...props}
          />
        );
        break;
    }

    return (
      <div
        className={
          styles["input-div"] +
          " " +
          startElementClassName +
          endElementClassName +
          className
        }
      >
        {inputNode}
        <div className={styles["start-element"]}>{startElement}</div>
        <div className={styles["end-element"]}>{endElement}</div>
      </div>
    );
  },
);

Input.displayName = "Input";
export default Input;
